import {Geometry} from './geometry';

export class RectangleGeometry extends Geometry{
  constructor(
    x: number,
    y: number,
    rotation: number,
    public width: number,
    public height: number
  ) {
    super(x,y,rotation);
  }
}
