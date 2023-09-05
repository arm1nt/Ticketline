import {TicketStatus} from '../enums/ticket-status';

export class Ticket {
  id: number;
  ticketId: string;
  ticketStatus: TicketStatus;
  seat?: number;
  stand?: number;
  price?: number;

  constructor(id: number, ticketId: string, ticketStatus: TicketStatus) {
    this.id = id;
    this.ticketId = ticketId;
    this.ticketStatus = ticketStatus;
  }
}
