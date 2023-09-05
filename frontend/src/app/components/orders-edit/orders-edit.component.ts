import {Component, Inject, OnInit} from '@angular/core';
import {Order} from '../../dtos/order';
import {Performance} from '../../dtos/performance';
import {Event} from '../../dtos/event';
import {AuthService} from '../../services/auth.service';
import {OrderService} from '../../services/order.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, Router} from '@angular/router';
import {EventService} from '../../services/event.service';
import {PerformanceService} from '../../services/performance.service';
import {SelectionModel} from '@angular/cdk/collections';
import {Ticket} from '../../dtos/ticket';
import {MatTableDataSource} from '@angular/material/table';
import {TicketStatus} from '../../enums/ticket-status';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';

export interface DialogData {
  action: string;
  tickets: Ticket[];
}

@Component({
  selector: 'app-orders-edit',
  templateUrl: './orders-edit.component.html',
  styleUrls: ['./orders-edit.component.scss']
})
export class OrdersEditComponent implements OnInit {

  order: Order;
  performance: Performance = new Performance();
  event: Event = new Event();
  displayedColumns: string[];

  initialSelection = [];
  allowMultiSelect = true;

  selection = new SelectionModel<Ticket>(this.allowMultiSelect, this.initialSelection);
  dataSource: MatTableDataSource<Ticket>;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private orderService: OrderService,
    private eventService: EventService,
    private performanceService: PerformanceService,
    private snackbar: MatSnackBar,
    private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.displayedColumns = ['select', 'ticketId', 'ticketStatus', 'price'];
    this.getOrderData(Number(this.route.snapshot.paramMap.get('id')));
  }

  getOrderData(id: number): void {
    this.orderService.getById(id).subscribe({
      next: data => {
        this.order = data;
        this.getPerformanceData(data.performanceId);
        this.dataSource = new MatTableDataSource<Ticket>(data.tickets);
        console.log(data);
      },
      error: error => {
        console.log(error);
        this.openSnackBar(`Error retrieving order`, 'Dismiss', false);
      }
    });
  }

  getEventData(id: number): void {
    this.eventService.getById(id).subscribe({
      next: data => {
        this.event = data;
        console.log(data);
      },
      error: error => {
        console.log(error);
        this.openSnackBar(`Error retrieving event`, 'Dismiss', false);
      }
    });
  }

  getPerformanceData(id: number): void {
    this.performanceService.getById(id).subscribe({
      next: data => {
        this.performance = data;
        this.getEventData(data.eventId);
        console.log(data);
      },
      error: error => {
        console.log(error);
        this.openSnackBar(`Error retrieving performance`, 'Dismiss', false);
      }
    });
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.dataSource.data.forEach(row => this.selection.select(row));
    }
  }

  toTimesStampSubtitle(date: Date): string {
    return new Date(date).toLocaleDateString() + ' at ' + new Date(date).toLocaleTimeString();
  }

  isInPast(d: Date): boolean {
    return new Date(Date.now()) > new Date(d);
  }

  cancelTickets(): void {
    const selected = structuredClone(this.selection.selected);
    selected.forEach(t => {
      t.ticketStatus = TicketStatus.cancelled;
    });
    const o = structuredClone(this.order);
    o.tickets = selected;
    this.orderService.update(o).subscribe(order => {
      this.order = order;
      this.selection.clear();
      this.getOrderData(this.order.id);
      this.router.navigate(['/orders']);
      this.openSnackBar(`Successfully canceled tickets`, 'Dismiss', true);
    }, error => {
      console.error('Error while cancelling the tickets', error);
      this.openSnackBar(`Error cancelling tickets`, 'Dismiss', false);
    });
  }

  purchaseTickets(): void {
    const selected = structuredClone(this.selection.selected);
    selected.forEach(t => {
      t.ticketStatus = TicketStatus.purchased;
    });
    const o = structuredClone(this.order);
    o.tickets = selected;
    this.orderService.update(o).subscribe(order => {
      this.order = order;
      this.selection.clear();
      this.openSnackBar(`Successfully purchased tickets`, 'Dismiss', true);
      this.getOrderData(this.order.id);
      this.router.navigate(['/orders']);
    }, error => {
      console.error('Error while purchasing the tickets', error);
      this.openSnackBar(`Error purchasing tickets`, 'Dismiss', false);
    });
  }

  allTicketsOfStatus(tickets: Ticket[], status: TicketStatus): boolean {
    let result = true;
    tickets.forEach(t => {
      if (t.ticketStatus.toString() !== status) {
        result = false;
      }
    });
    return result;
  }

  allTicketsReserved(tickets: Ticket[]): boolean {
    return this.allTicketsOfStatus(tickets, TicketStatus.reserved);
  }

  allTicketsPurchased(tickets: Ticket[]): boolean {
    return this.allTicketsOfStatus(tickets, TicketStatus.purchased);
  }

  openSnackBar(message: string, action: string, success: boolean) {
    if(success) {
      this.snackbar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-success']});
    } else {
      this.snackbar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-error']});
    }
  }

  openCancelDialog() {
    const dialogRef = this.dialog.open(OrderEditDialogComponent, {
      data: {action: 'cancel', tickets: this.selection.selected},
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cancelTickets();
      }
    });
  }

  openPurchaseDialog() {
    const dialogRef = this.dialog.open(OrderEditDialogComponent, {
      data: {action: 'purchase', tickets: this.selection.selected},
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.purchaseTickets();
      }
    });
  }
}

@Component({
  selector: 'app-order-edit-dialog',
  templateUrl: 'order-edit-dialog.html',
})
export class OrderEditDialogComponent {
  constructor(public dialogRef: MatDialogRef<OrderEditDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }


  calcTicketTotal(): number {
    let total = 0;
    this.data.tickets.forEach(t => total += t.price);
    return total;
  }
}
