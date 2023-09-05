import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Seating} from '../../../../dtos/seating';
import {Seat} from '../../../../dtos/seat';
import {Ticket} from '../../../../dtos/ticket';
import { Observable } from 'rxjs';
import { Sector } from 'src/app/dtos/sector';
import { TicketStatus } from 'src/app/enums/ticket-status';

@Component({
  selector: 'app-seating',
  templateUrl: './seating.component.html',
  styleUrls: ['./seating.component.scss']
})
export class SeatingComponent implements OnInit {

  @Input() getUpdatedSector: Observable<Sector>;
  @Input() seating: Seating;
  @Output() chosenTicketsEvent = new EventEmitter<Ticket[]>();
  public chosenTickets: Ticket[];

  constructor() {
  }

  ngOnInit(): void {
    this.getUpdatedSector.subscribe((data) => this.updateSectorAndContainingSpots(data));
    this.chosenTickets = [];
    this.seating.rows.forEach((row) => {
      row.seats.forEach((seat) =>{
        if (seat.chosen) {
          this.chosenTickets.push(seat.ticket);
        }
      });
    });
    this.chosenTicketsEvent.emit(this.chosenTickets);
  }

  choseMe(seat: Seat) {
    seat.chosen = !seat.chosen;
    if (seat.chosen) {
      this.chosenTickets.push(seat.ticket);
    } else {
      for (let i = 0; i < this.chosenTickets.length; i++) {
        // Hope deep comparison is not necessary
        if (seat.ticket === this.chosenTickets[i]) {
          this.chosenTickets = this.chosenTickets.slice(0, i).concat(this.chosenTickets.slice(i + 1));
        }
      }
    }
    this.chosenTicketsEvent.emit(this.chosenTickets);
  }

  updateSectorAndContainingSpots(sector: any): void {

    const temp: Seating = sector;
    for (const tempRow of temp.rows) {
      for (const tempSeat of tempRow.seats) {
        for (const rowInDisplayed of this.seating.rows) {
          for (const seatInDisplayed of rowInDisplayed.seats) {
            if (seatInDisplayed.id === tempSeat.id) {
              if (tempSeat.ticket.ticketStatus === TicketStatus.reserved
                || tempSeat.ticket.ticketStatus === TicketStatus.purchased) {
                  seatInDisplayed.chosen = true;
                  seatInDisplayed.ticket.id = tempSeat.ticket.id;
                  seatInDisplayed.ticket.ticketStatus = tempSeat.ticket.ticketStatus;

                  for (let i = this.chosenTickets.length-1; i >= 0; i--) {
                    if (this.chosenTickets[i].id === tempSeat.ticket.id) {
                      this.chosenTickets.splice(i, 1);
                    }
                  }
                }
            }
          }
        }
      }
    }

    for (const chosenTicket of this.chosenTickets) {
      if (chosenTicket.ticketStatus !== TicketStatus.free) {
        chosenTicket.ticketStatus = TicketStatus.free;
      }
    }

    this.chosenTicketsEvent.emit(this.chosenTickets);
  }
}
