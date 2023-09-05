import {Component, OnInit} from '@angular/core';
import {Seat} from '../../../dtos/seat';
import {SeatGeometry} from '../../../dtos/seat-geometry';
import {TicketStatus} from '../../../enums/ticket-status';
import {Ticket} from '../../../dtos/ticket';

@Component({
  selector: 'app-seat-legend',
  templateUrl: './seat-legend.component.html',
  styleUrls: ['./seat-legend.component.scss']
})
export class SeatLegendComponent implements OnInit {

  reservedSeat: Seat;
  boughtSeat: Seat;
  freeSeat: Seat;
  chosenSeat: Seat;

  constructor() { }

  ngOnInit(): void {
    const standardSeat = new SeatGeometry(
      0,
        12,
        0,
        30,
        30,
        12
      );
    this.reservedSeat = new Seat(0,'', new Ticket(-1, '', TicketStatus.reserved), standardSeat, false);
    this.boughtSeat = new Seat(0,'',  new Ticket(-1, '', TicketStatus.purchased), standardSeat, false);
    // TODO: Make them non-clickable
    this.freeSeat = new Seat(0,'', new Ticket(-1, '', TicketStatus.free), standardSeat, false);
    this.chosenSeat = new Seat(0,'', new Ticket(-1, '', TicketStatus.free), standardSeat, true);
  }

}
