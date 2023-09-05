import {Event, EventInfo} from './event';

export class Performer {
  id: number;
  firstName: string;
  lastName: string;
  performerName: string;
  events: Event[];
}

export class PerformerSearchResult {
  performer: Performer;
  events: EventInfo[];
}
