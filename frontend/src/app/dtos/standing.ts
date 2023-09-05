import {Stand} from './stand';
import {Sector} from './sector';
import {RectangleGeometry} from './rectangle-geometry';

export class Standing extends Sector {
  constructor(
    id: number,
    sectorId: string,
    price: number,
    color: string,
    geometry: RectangleGeometry,
    public capacity: number,
    public stands: Stand[]
) {
    super(id, sectorId, price, color, geometry);
  }
}
