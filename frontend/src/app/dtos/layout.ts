import {EventHall} from './event-hall';
import {Sector, SectorCreate} from './sector';

export class CreateLayout {
  name: string;
  sectors: SectorCreate[];
}

export class LayoutOverview {
  id: number;
  name: string;
  eventhallName: string;
}


export class Layout {
  constructor(
    public id: number,
    public eventHall: EventHall,
    public sectors: Sector[]
  ) {
  }
}
