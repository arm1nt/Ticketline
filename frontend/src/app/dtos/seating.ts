import {Row} from './row';
import {Sector} from './sector';
import {RectangleGeometry} from './rectangle-geometry';

export class Seating extends Sector {
  constructor(
    id: number,
    sectorId: string,
    price: number,
    color: string,
    geometry: RectangleGeometry,
    public rows: Row[]
  ) {
    super(id, sectorId, price, color, geometry);
  }
}
