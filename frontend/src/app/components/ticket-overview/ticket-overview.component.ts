import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {Sector} from '../../dtos/sector';
import {Ticket} from '../../dtos/ticket';
import {Seating} from '../../dtos/seating';

@Component({
  selector: 'app-ticket-overview',
  templateUrl: './ticket-overview.component.html',
  styleUrls: ['./ticket-overview.component.scss']
})
export class TicketOverviewComponent implements OnInit {

  @Input() chosenTickets: Map<Sector, Ticket[]>;
  constructor() { }

  ngOnInit(): void {
  }
  isSeating(sector: Sector): boolean {
    return sector instanceof Seating;
  }
  noTicketSelected(): boolean {
    let areTicketsSelected = true;
    if (this.chosenTickets !== undefined) {
      this.chosenTickets.forEach((ticket) => {
        if (ticket.length !== 0) {
          areTicketsSelected = false;
        }
      });
    }
    return areTicketsSelected;
  }
  getTotalSum(): number {
    let sum = 0;
    this.chosenTickets.forEach((ticket, sector) => {
      sum += sector.price * ticket.length;
    });
    return sum;
  }
}
