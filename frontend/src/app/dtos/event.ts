import {EventType} from '../enums/event-type';
import {Performer} from './performer';
import {Performance, PerformanceInfo} from './performance';

export class Event {
  id: number;
  name: string;
  eventType: EventType;
  image: any;
  performers: Performer[];
  performances: Performance[];
  duration: number;
  soldTickets: number;
}

export class EventInfo {
  id: number;
  name: string;
  eventType: EventType;
  image: any;
  performances: PerformanceInfo[];
}
