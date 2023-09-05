import { GridElement } from './gridElement';
import { RectangleGeometry } from './rectangle-geometry';
import { RowCreate } from './row';


export class SectorCreate {
    id: number;
    type: string;
    price: number;
    color: string;
    capacity?: number;
    x: number;
    y: number;
    width: number;
    height: number;
    rows: RowCreate[];
    topLeft: GridElement;
    colorSector: number;
}


export abstract class Sector {
  protected constructor(
  public id: number,
  public sectorId: string,
  public price: number,
  public color: string,
  public geometry: RectangleGeometry
  ) {}
}
