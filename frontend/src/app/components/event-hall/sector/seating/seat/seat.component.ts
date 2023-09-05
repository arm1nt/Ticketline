import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Seat} from '../../../../../dtos/seat';
import {TicketStatus} from '../../../../../enums/ticket-status';

@Component({
  selector: 'app-seat',
  templateUrl: './seat.component.html',
  styleUrls: ['./seat.component.scss']
})
/**
 *
 */
export class SeatComponent implements OnInit {

  @Input() seat: Seat;
  @Input() clickable?: boolean;
  @Output() chosenEvent = new EventEmitter<boolean>();

  public borderWidth: number;
  public chosen: boolean;

  constructor() {
    this.borderWidth = 4;
  }

  ngOnInit(): void {
    if (this.seat.ticket.ticketStatus !== TicketStatus.free && this.seat.chosen) {
      alert('A seat with an seat status cannot be chosen!');
    }
  }

  getSeatColor(): string {
    if (this.seat.ticket.ticketStatus === TicketStatus.reserved) {
      return 'lightgray';
    } else if (this.seat.ticket.ticketStatus === TicketStatus.purchased) {
      return 'lightblue';
    } else if (this.seat.chosen) {
      return 'yellow';
    } else {
      return 'white';
    }
  }
  chooseThisSeat() {
    if (this.seat.ticket.ticketStatus === TicketStatus.free) {
      this.chosen = !this.chosen;
      this.chosenEvent.emit(this.chosen);
    }
  }
  seatClickable(seat: Seat): boolean {
    return (this.clickable === undefined || this.clickable === true) && seat.ticket.ticketStatus === TicketStatus.free;
  }
}
