import {Component, OnInit} from '@angular/core';
import {Order} from '../../dtos/order';
import {BookingService} from '../../services/booking.service';
import {TicketStatus} from '../../enums/ticket-status';
import {ActivatedRoute, Router} from '@angular/router';
import {OrderService} from 'src/app/services/order.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-reservation-successful',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.scss']
})
export class SuccessComponent implements OnInit {

  order: Order;
  returnedId: number;
  canceled = true;

  constructor(private service: BookingService,
              private route: ActivatedRoute,
              private orderService: OrderService,
              private router: Router,
              private snackbar: MatSnackBar) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void {
    this.getOrder();
  }

  id(): number {
    // @ts-ignore
    return this.route.snapshot.paramMap.get('id');
  }

  getOrder(): void {

    this.service.getById(this.id()).subscribe({
      next: order => {
        this.order = order;
      },
      error: error => {
        if (error.status === 403) {
          this.snackbar.open('There exists no such order', null, {
            duration: 1500,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
          this.router.navigate(['/orders']);
        } else {
          this.snackbar.open('There was an error retrieving the order details', null, {
            duration: 1500,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
          this.router.navigate(['/orders']);
        }
      }
    });

    this.service.getById(this.id()).subscribe(order => this.order = order, error => {
      console.error('Error loading order', error);
    });
  }

  cancelOrder(): void {
    this.order.tickets.forEach((ticket) => {
      ticket.ticketStatus = TicketStatus.cancelled;
    });
    this.service.update(this.order).subscribe(order => {
      this.order = order;
    }, error => {
      console.error('Error while canceling the order', error);
    });
  }

  buyOrder(): void {

    this.order.tickets.forEach((ticket) => {
      ticket.ticketStatus = TicketStatus.purchased;
    });

    this.service.update(this.order).subscribe({
      next: order => {
        this.order = order;
        this.router.navigate(['/success/' + this.order.id]);
      },
      error: error => {
        this.snackbar.open('There was an error purchasing the reserved tickets', null, {
          duration: 1500,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });

    /*this.service.update(this.order).subscribe(order => this.order = order, error => {
      console.error('Error while purchasing the order', error);
    });*/
  }


  getPdf(x: number): void {
    this.orderService.getInvoice(this.order.id).subscribe(
      data => {
        const byteCharacters = atob(data.pdf);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const file = new Blob([byteArray], {type: 'application/pdf;base64'});
        const fileURL = URL.createObjectURL(file);
        window.open(fileURL);
      }
    );
  }

  getCancellation(x: number): void {
    this.orderService.getCancellation(this.order.id).subscribe({
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
        this.snackbar.open('There was an error retrieving the cancellation invoice', null, {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }


}
