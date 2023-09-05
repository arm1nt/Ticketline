import {Ticket} from './ticket';
import {Performer} from './performer';

export class OrderOverview {

  id: number;
  tickets: Ticket[];
  reservationCode?: string;
  dueTime?: Date;
  time?: Date;
  total?: number;
  performanceId: number;
  performanceName: string;
  eventId: number;
  eventName: string;
  performers: Performer[];
  performanceStartTime: Date;
  locationName: string;
  eventHallName: string;
}
