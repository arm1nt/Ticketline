import {Event} from './event';
import {Band} from './band';

export class Artist {
  id: number;
  firstName: string;
  lastName: string;
  performerName: string;
  bands: Band[];
  events: Event[];
}
