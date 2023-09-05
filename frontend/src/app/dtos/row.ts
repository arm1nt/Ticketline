import { CreateSeate, Seat } from './seat';
import {Geometry} from './geometry';

export class RowCreate {
    number: number;
    seats: CreateSeate[];
    x: number;
    y: number;
}


export class Row {
  constructor(
  public id: number,
  public rowNumber: string,
  public seats: Seat[],
  public geometry: Geometry
  ) {}
}
