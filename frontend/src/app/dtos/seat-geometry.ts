import {RectangleGeometry} from './rectangle-geometry';

export class SeatGeometry extends RectangleGeometry {
  // All numbers should be given in pixel
  constructor(
    x: number,
    y: number,
    rotation: number,
    width: number,
    height: number,
    public legSpaceDepth: number,
  ) {
    super(x, y, rotation, width, height);
  }
}
