import {EventType} from '../enums/event-type';

export class PerformanceSearch {
  time: Date;
  minPrice: number;
  maxPrice: number;
  performanceName: string;
  eventName: string;
  hallName: string;
  eventType: EventType;
}

export class PerformanceSearchResult {
  id: number;
  eventName: string;
  performanceName: string;
  startTime: string;
  endTime: string;
}
