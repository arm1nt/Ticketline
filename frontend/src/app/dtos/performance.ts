import {EventHall} from './event-hall';

export class Performance {
  id: number;
  performanceName: string;
  startTime: Date;
  endTime: Date;
  layoutId: number;
  eventId: number;
  locationId: number;
  eventhallId: number;
}

export class PerformanceInfo {
  id: number;
  performanceName: string;
  startTime: Date;
  endTime: Date;
  eventHall: EventHall;
}
