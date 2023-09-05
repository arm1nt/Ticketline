import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {OrderService} from '../../services/order.service';
import {OrderOverview} from '../../dtos/orderoverview';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TicketsService} from 'src/app/services/tickets.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})

export class OrdersComponent implements OnInit {

  orders: OrderOverview[];
  displayedColumns: string[];


  constructor(
    private authService: AuthService,
    private orderService: OrderService,
    private ticketService: TicketsService,
    private snackbar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.displayedColumns = ['ticketId', 'ticketStatus', 'price'];
    this.loadAllOrders();
  }

  loadAllOrders() {
    this.orderService.getAllOrders().subscribe({
      next: (orders: OrderOverview[]) => {
        this.orders = orders;
        console.log(orders);
      },
      error: error => {
        console.log(error);
      }
    });
  }

  toTimesStamp(date: Date): string {
    return new Date(date).toLocaleDateString() + ' at ' + new Date(date).toLocaleTimeString().substring(0, 5);
  }

  getPdf(x: number): void {
    this.orderService.getInvoice(x).subscribe({
      next: data => {
        const byteCharacters = atob(data.pdf);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const file = new Blob([byteArray], {type: 'application/pdf;base64'});
        const fileURL = URL.createObjectURL(file);
        window.open(fileURL);
      },
      error: error => {
        console.log(error);
        this.snackbar.open('There was an error retrieving the invoice', null, {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  getCancellation(x: number): void {
    this.orderService.getCancellation(x).subscribe({
      next: data => {
        const byteCharacters = atob(data.pdf);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const file = new Blob([byteArray], {type: 'application/pdf;base64'});
        const fileURL = URL.createObjectURL(file);
        window.open(fileURL);
      },
      error: error => {
        console.log(error);
        this.snackbar.open('There was an error retrieving the cancellation invoice', null, {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  downloadTicketPdf(id: number) {
    this.ticketService.getTicket(id).subscribe(
      data => {
        const byteCharacters = atob(data.pdf);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const file = new Blob([byteArray], { type: 'application/pdf;base64' });
        const fileURL = URL.createObjectURL(file);
        window.open(fileURL);
      }
    );
  }

  isInPast(d: Date): boolean {
    return new Date(Date.now()) > new Date(d);
  }
}

