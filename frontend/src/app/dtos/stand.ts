import {Ticket} from './ticket';

export class Stand {
  constructor(
    public id: number,
    public ticket: Ticket,
    public chosen: boolean
  ) {
  }
}
