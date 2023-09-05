import {Ticket} from './ticket';

export class Order {

  id: number;
  tickets: Ticket[];
  reservationCode?: string;
  dueTime?: Date;
  time?: Date;
  total?: number;
  performanceId: number;
}

export class Invoice {
  pdf: string;
}





