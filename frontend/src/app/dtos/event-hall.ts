import {CreateLayout} from './layout';
import {Location} from './location';
import {RectangleGeometry} from './rectangle-geometry';

export class EventHall {
  constructor(
    public id: number,
    public name: string,
    public geometry: RectangleGeometry,
    public location: Location
  ) {
  }
}

export class CreateEventHall {
  id: number;
  name: string;
  location: Location;
  x: number;
  y: number;
  width: number;
  height: number;
  layout: CreateLayout;
}

export class EventHallOverview {
  id: number;
  name: string;
  location: Location;
}

export class TemporaryEventHallInformation {
  name: string;
  location: Location;
}



