import {SeatGeometry} from './seat-geometry';
import {Ticket} from './ticket';

export class Seat {
  public constructor(
    public id: number,
    public seatId: string,
    public ticket: Ticket,
    public geometry: SeatGeometry,
    public chosen?: boolean
    // In radiant
  ) {
  }
}


export class CreateSeate {
  seatId: string;
  rowNumber: number;
  x: number;
  y: number;
  price: number;
  polygonPoints: string;
  sectorId: number;
  beSectorId: number;
  chosen: boolean;
}
