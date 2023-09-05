import {Event} from './event';
import {Artist} from './artist';

export class Band {
  id: number;
  performerName: string;
  artists: Artist[];
  events: Event[];
}
