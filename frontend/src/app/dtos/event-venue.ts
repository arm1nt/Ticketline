import {Seat} from './seat';

export class EventVenue {
  constructor(
    public name: string,
    public location: string,
    public seats: Seat[]
  ) {
  }
}
