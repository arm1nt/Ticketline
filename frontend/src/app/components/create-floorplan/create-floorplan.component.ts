import { Component, OnInit, ViewChild, HostListener, ElementRef, AfterViewInit, Input } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { CreateEventHall, EventHall, TemporaryEventHallInformation } from 'src/app/dtos/event-hall';
import { GridElement } from 'src/app/dtos/gridElement';
import { CreateLayout } from 'src/app/dtos/layout';
import { Place } from 'src/app/dtos/place';
import { RowCreate } from 'src/app/dtos/row';
import { CreateSeate } from 'src/app/dtos/seat';
import { Sector, SectorCreate } from 'src/app/dtos/sector';
import { LayoutService } from 'src/app/services/layout.service';
import { SeatingSectorDialogComponent } from './floorplan-dialogs/seating-dialog/seating-sector-dialog/seating-sector-dialog.component';
import { StandingSectorDialogComponent } from './floorplan-dialogs/standing-dialog/standing-sector-dialog/standing-sector-dialog.component';
import {CdkDrag, DragDropModule} from '@angular/cdk/drag-drop';
import { EventhallService } from 'src/app/services/eventhall.service';
import { HelpDialogComponent } from './floorplan-dialogs/tool-description/help-dialog/help-dialog.component';


export interface StandingData {
  id: number;
  capacity: number;
  price: number;
}

export interface SeatingData {
  id: number;
  price: number;
}

@Component({
  selector: 'app-create-floorplan',
  templateUrl: './create-floorplan.component.html',
  styleUrls: ['./create-floorplan.component.scss']
})
export class CreateFloorplanComponent implements OnInit {


  @ViewChild('svg', {static: true}) svg: ElementRef<SVGSVGElement>;

  @Input() generalEventHallInformation: TemporaryEventHallInformation;

  @Input() inputEventHall: number = undefined;

  // if we add a layout to an existing eventhall this is the eventhall with its boundaries
  parentEventHall: EventHall = undefined;
  addingLayout = false;


  // To send to backend:
  backendSectors: SectorCreate[] = [];

  // redraw seats after all sectors have been removed, while preserving the seat selection
  redrawSeats = false;
  placesToReDraw = [[]];

  // name of the layout
  layoutName = '';


  // To add new sectors and store them
  sectors: SectorCreate[] = [];
  createNewSector = false;
  startIndex = 0;
  currentlySelectedSector = 0;
  creationSectorType = '';
  creationSectorPrice?: number = null;
  colors: string[] = [];
  colorsSector: string[] = [];
  sectorTypes = ['Seating', 'Standing'];

  // Styling of the svg
  svgWidth = 560;
  svgHeight = 560;

  // seats
  places: Place[][] = [[]];

  // switch between selecting seats and
  makeSectors = false;

  // for changing colors
  panelOpenState = false;

  // grid to snap the sectors to so it look uniform
  // maybe allow user to disable snapping or offer some settings to tweak it
  grid: GridElement[] = [];

  // To draw rectangle
  isDrawing = false;
  startX: number;
  startY: number;
  rect: SVGRectElement;
  enumerate = 0;

  constructor(
    private snackbar: MatSnackBar,
    private layoutService: LayoutService,
    private eventHallService: EventhallService,
    private router: Router,
    private dialog: MatDialog) { }

  onMouseDown(event: MouseEvent): void {

    if (this.isDrawing) {
      return;
    }

    if (!this.makeSectors) {
      return;
    }


    this.isDrawing = true;

    const canvasElement = document.getElementById('floorplan-canvas');
    const boundingRectangle = canvasElement.getBoundingClientRect();

    let x = event.clientX;
    let y = event.clientY;

    x = x - boundingRectangle.left;
    y = y - boundingRectangle.top;

    this.startX = x;
    this.startY = y;


    this.rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
    this.rect.style.stroke = this.colors[this.currentlySelectedSector];

    this.rect.style.fill = 'transparent';
    this.rect.style.pointerEvents = 'all';
    this.rect.style.userSelect = 'all';
    this.rect.style.position = 'relative';
    this.rect.style.zIndex = '0';
    this.rect.classList.add('sector');
    this.rect.classList.add(`sector-${this.currentlySelectedSector}`);
    this.rect.style.pointerEvents = 'all';
    this.rect.style.userSelect = 'all';
    this.rect.onmousedown = (event2: Event) => {
      this.dosmth(event2);
    };
    this.svg.nativeElement.appendChild(this.rect);
  }

  onMouseMove(event: MouseEvent): void {
    if (!this.makeSectors) {
      return;
    }


    if (this.isDrawing) {
      const canvasElement = document.getElementById('floorplan-canvas');
      const boundingRectangle = canvasElement.getBoundingClientRect();
      const x = Math.min(event.clientX - boundingRectangle.left, this.startX);
      const y = Math.min(event.clientY - boundingRectangle.top, this.startY);
      const width = Math.abs(event.clientX - this.startX -  boundingRectangle.left);
      const height = Math.abs(event.clientY - this.startY -  boundingRectangle.top);
      this.rect.setAttribute('x', x.toString());
      this.rect.setAttribute('y', y.toString());
      this.rect.setAttribute('width', width.toString());
      this.rect.setAttribute('height', height.toString());
      this.rect.style.fill = 'transparent';
    }
}

  onMouseUp(event: MouseEvent): void {

    if (!this.makeSectors) {
      return;
    }

    this.isDrawing = false;


    // if the size is too small to be usefull, e.g. sector smaller than a seat (maybe change to, if not overlapping with a seat)
    const size = this.rect.getBoundingClientRect();

    if (Math.abs(size.left - size.right) <= 25 || Math.abs(size.top - size.bottom) <= 25) {
      this.rect.remove();
      return;
    }

    const canvasElement = document.getElementById('floorplan-canvas');
    const boundingRectangle = canvasElement.getBoundingClientRect();

    const left = size.left - boundingRectangle.left;
    const right = size.right - boundingRectangle.left;
    const bottom = size.bottom - boundingRectangle.top;
    const top = size.top - boundingRectangle.top;


    // snap to grid
    let minForTopLeft: GridElement;
    let mindDistanceForTopLeft = Number.MAX_SAFE_INTEGER;
    let minForBottomRight: GridElement;
    let minDistanceForBottomRight = Number.MAX_SAFE_INTEGER;
    for (const el of this.grid) {
      if (this.euclideanDistance(left, top, el.x, el.y) < mindDistanceForTopLeft) {
        mindDistanceForTopLeft = this.euclideanDistance(left, top, el.x, el.y);
        minForTopLeft = el;
      }

      if (this.euclideanDistance(right, bottom, el.x, el.y) < minDistanceForBottomRight) {
        minDistanceForBottomRight = this.euclideanDistance(right, bottom, el.x, el.y);
        minForBottomRight = el;
      }
    }

    this.rect.setAttribute('x', (minForTopLeft.x).toString());
    this.rect.setAttribute('y', (minForTopLeft.y).toString());


    const newWidth = Math.floor((Math.abs(minForTopLeft.x - minForBottomRight.x))); // - 2.5;
    const newHeight = Math.floor(Math.abs(minForTopLeft.y - minForBottomRight.y)); // - 2.5;
    this.rect.setAttribute('width', newWidth.toString());
    this.rect.setAttribute('height', newHeight.toString());


    const newCenterX = (minForTopLeft.x + (newWidth / 2));
    const newCenterY = (minForTopLeft.y) + (newHeight / 2);

    for (const row of this.places) {
      for (const seat of row) {
        if (seat.xCord >= minForTopLeft.x && seat.xCord <= minForBottomRight.x
          && seat.yCord >= minForTopLeft.y && seat.yCord <= minForBottomRight.y) {
            if (seat.sectorId !== -1) {
              this.snackbar.open('Overlapping with other sector', 'Dismiss', {
                duration: 1300,
                horizontalPosition: 'right',
                verticalPosition: 'top',
                panelClass: ['snack-error']
              });
              this.rect.remove();
              return;
            }
          }
      }
    }



    this.enumerate++;

    const beAddSector: SectorCreate = {
      id: this.enumerate,
      type: this.sectors[this.currentlySelectedSector].type,
      price: this.sectors[this.currentlySelectedSector].price,
      color: this.colors[this.currentlySelectedSector],
      capacity: 20,
      x: newCenterX,
      y: newCenterY,
      width: newWidth,
      height: newHeight,
      rows: [],
      topLeft: minForTopLeft,
      colorSector: this.currentlySelectedSector
    };

    const beAddRows: RowCreate[] = [];

    if (this.sectors[this.currentlySelectedSector].type === 'Seating') {

      // eslint-disable-next-line @typescript-eslint/prefer-for-of
      for (let i = 0; i < this.places.length; i++) {
        // eslint-disable-next-line @typescript-eslint/prefer-for-of
        for (let j = 0; j < this.places[i].length; j++) {
          if ((this.places[i][j].xCord >= minForTopLeft.x && this.places[i][j].xCord <= minForBottomRight.x)
                && (this.places[i][j].yCord >= minForTopLeft.y && this.places[i][j].yCord <= minForBottomRight.y)) {



            this.places[i][j].sectorId = this.currentlySelectedSector;
            this.places[i][j].beSectorId = this.enumerate;

            let insertedRow = -1;

            // eslint-disable-next-line @typescript-eslint/prefer-for-of
            for (let k = 0; k < beAddRows.length; k++) {
              if (this.places[i][j].yCord === beAddRows[k].seats[0].y) {
                insertedRow = k;
                const beAddSeat: CreateSeate = {
                  seatId: this.places[i][j].id,
                  rowNumber: k,
                  x: this.places[i][j].xCord,
                  y: this.places[i][j].yCord,
                  price: this.sectors[this.currentlySelectedSector].price,
                  polygonPoints: this.places[i][j].polygonPoints,
                  sectorId: this.sectors[this.currentlySelectedSector].id,
                  beSectorId: this.enumerate,
                  chosen: this.places[i][j].selected
                };
                beAddRows[k].seats.push(beAddSeat);
              }
            }

            if (insertedRow === -1) {
              const addRow: RowCreate = {
                number: (beAddRows.length + 1),
                seats: [],
                x: -100,
                y: -100
              };
              const beAddSeat: CreateSeate = {
                seatId: this.places[i][j].id,
                rowNumber: addRow.number,
                x: this.places[i][j].xCord,
                y: this.places[i][j].yCord,
                price: this.sectors[this.currentlySelectedSector].price,
                polygonPoints: this.places[i][j].polygonPoints,
                sectorId: this.sectors[this.currentlySelectedSector].id,
                beSectorId: this.enumerate,
                chosen: this.places[i][j].selected
              };
              addRow.seats.push(beAddSeat);
              beAddRows.push(addRow);
            }

            const seathHeight = 30 / 2;
            const seatWidth = 30 / 2;
            const seatLegspace = 12;

            const runningX = this.places[i][j].xCord;
            const runningY = this.places[i][j].yCord;

            const x1 = runningX - seatWidth;
            const y1 = runningY - seathHeight;

            const x2 = runningX - seatWidth;
            const y2 = runningY + seathHeight;

            const x3 = runningX + seatWidth;
            const y3 = runningY + seathHeight;

            const x4 = runningX + seatWidth;
            const y4 = runningY - seathHeight;

            const x5 = runningX;
            const y5 = runningY - (seathHeight + seatLegspace);

            document.getElementById(this.places[i][j].id).remove();

            const polygon = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
            polygon.setAttribute('points', `${x1},${y1}  ${x2},${y2} ${x3},${y3} ${x4},${y4}  ${x5},${y5}`);
            polygon.id = this.places[i][j].id;

            if (this.places[i][j].selected) {
              polygon.style.fill = 'lightgray';
            } else {
              polygon.style.fill = 'white';
            }
            this.places[i][j].sectorId = this.currentlySelectedSector;
            polygon.style.strokeWidth = '4px';
            polygon.setAttribute('x', x1.toString());
            polygon.setAttribute('y', y1.toString());
            polygon.classList.add('spot');
            polygon.style.stroke = 'black';
            polygon.style.userSelect = 'none';
            polygon.style.pointerEvents = 'none';
            polygon.onmousedown = ( (event2: Event) => {
              this.chooseSpot((event2.target as Element).id, event2);
              event2.stopPropagation();

            } );
            canvasElement.appendChild(polygon);
          }
        }
      }
    } else if (this.sectors[this.currentlySelectedSector].type === 'Standing') {
      for (let i = 0; i < this.places.length; i++) {
        for (let j = 0; j < this.places[i].length; j++) {
          if ((this.places[i][j].xCord >= minForTopLeft.x && this.places[i][j].xCord <= minForBottomRight.x)
                && (this.places[i][j].yCord >= minForTopLeft.y && this.places[i][j].yCord <= minForBottomRight.y)) {
            this.places[i][j].selected = false;
            this.places[i][j].sectorId = this.currentlySelectedSector;
            this.places[i][j].beSectorId = this.enumerate;
            const elem = document.getElementById(`${i},${j}`);
            elem.style.fill = 'white';
          }
        }
      }
    }

    const rowEnums = document.getElementsByClassName('rowEnum');
    for (let i = rowEnums.length - 1; i >= 0; i--) {
      const enumX = parseInt(rowEnums[i].id.split(',')[0], 10);
      const enumY = parseInt(rowEnums[i].id.split(',')[1], 10);

      const enumHeightTop = newCenterY - newWidth / 2;
      const enumgHeightBottom = newCenterY + newWidth / 2;

      const overdrawEnum = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        overdrawEnum.id = `${enumX}, ${enumY}`;
        overdrawEnum.setAttribute('class', 'rowEnum');
        overdrawEnum.setAttribute('x', enumX.toString());
        overdrawEnum.setAttribute('y', enumY.toString());
        overdrawEnum.style.height = '24';
        overdrawEnum.style.width = '8.6167';
        overdrawEnum.innerHTML = rowEnums[i].innerHTML;
        overdrawEnum.style.userSelect = 'none';
        overdrawEnum.style.pointerEvents = 'none';

        rowEnums[i].remove();
        canvasElement.appendChild(overdrawEnum);
    }

    beAddSector.rows = beAddRows;
    this.backendSectors.push(beAddSector);



    this.rect.style.fill = this.colors[this.currentlySelectedSector];

    this.rect.id = `${this.enumerate}`;
    this.rect.onmouseup = (event2: Event) => {
      this.dosmth2(event2);
    };

    this.isDrawing = false;
    this.rect = null;
  }

  isSeatingSector(id: number): boolean {
    for (const sec of this.sectors) {
      if (sec.id === id && (sec.type === 'Seating')) {
        return true;
      }
    }
    return false;
  }

  isStandingSector(id: number): boolean {
    for (const sec of this.sectors) {
      if (sec.id === id && (sec.type === 'Standing')) {
        return true;
      }
    }
    return false;
  }

  euclideanDistance(x1: number, y1: number, x2: number, y2: number): number {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }


  ngOnInit(): void {
    if (this.inputEventHall !== undefined) {
      this.addingLayout = true;
      this.eventHallService.getById(this.inputEventHall).subscribe({
        next: data => {

          this.parentEventHall = data;
          this.svgHeight = data.geometry.height;
          this.svgWidth = data.geometry.width;
          this.createGrid();
          this.drawSeats();
          this.centerScene();
        },
        error: error => {
          this.snackbar.open(`${error.error.error}`, 'Dismiss', {
            duration: 1300,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
          this.router.navigate(['/']);
        }
      });
    } else {
      this.createGrid();
      this.drawSeats();
    }



  }


  dosmth(e: Event): void {
    e.stopPropagation();

    const querySelectorImitation = ((document.getElementById((e.target as Element).id)) as Element);
    const classes = querySelectorImitation.classList;
    const index = parseInt(classes[1].split('-')[1], 10);


    let currCapacity = -1;
    for (let i = 0; i < this.backendSectors.length; i++) {
      if (this.backendSectors[i].id === (parseInt((e.target as Element).id, 10))) {
        currCapacity = i;
        break;
      }
    }


    if (this.sectors[index].type === 'Standing') {
      this.dialog.open(StandingSectorDialogComponent, {
        data: {
          id: index,
          price: this.sectors[index].price,
          capacity: this.backendSectors[currCapacity].capacity
        },
        width: '40em',
        height: '26em',
        maxWidth: '1200px'
      }).afterClosed().subscribe(response => {

        if (response === undefined) {
          return;
        }

        if (response.delete) {
          const elem = document.getElementById((e.target as Element).id);
          elem.remove();

          for (let i = 0; i < this.backendSectors.length; i++) {
            if (this.backendSectors[i].id === (parseInt((e.target as Element).id, 10))) {

              for (const tempRow of this.places) {
                for (const tempSeat of tempRow) {
                  if (tempSeat.beSectorId === (parseInt((e.target as Element).id, 10))) {
                    tempSeat.beSectorId = -1;
                    tempSeat.sectorId = -1;
                  }
                }
              }

              this.backendSectors.splice(i, 1);
              break;
            }
          }
        } else if (response.changes) {

          for (const changeSectorCapactiy of this.backendSectors) {
            if (changeSectorCapactiy.id === parseInt((e.target as Element).id, 10)) {
              changeSectorCapactiy.capacity = response.capacity;
            }
          }

          this.sectors[index].price = response.price;
          for (const changeSectorPrice of this.backendSectors) {
            if (changeSectorPrice.colorSector === index) {
              changeSectorPrice.price = response.price;
            }
          }

          this.snackbar.open('Sucessfully updated sector', 'Dismiss', {
            duration: 1500,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-success']
          });
        }
      });
    } else {
      this.dialog.open(SeatingSectorDialogComponent, {
        data: {
          id: index,
          price: this.sectors[index].price,
        },
        width: '40em',
        height: '21em',
        maxWidth: '1200px'
      }).afterClosed().subscribe(response => {
        if (response === undefined) {
          return;
        }

        if (response.delete) {

          for (let i = 0; i < this.backendSectors.length; i++) {
            if (this.backendSectors[i].id === (parseInt((e.target as Element).id, 10))) {

              for (const tempRow of this.places) {
                for (const tempSeat of tempRow) {
                  if (tempSeat.beSectorId === (parseInt((e.target as Element).id, 10))) {
                    tempSeat.beSectorId = -1;
                    tempSeat.sectorId = -1;
                  }
                }
              }

              this.backendSectors.splice(i, 1);
            }
          }
          const elem = document.getElementById((e.target as Element).id);
          elem.remove();
        }

        if (response.price !== undefined) {
          this.sectors[index].price = response.price;
          for (const changeSectorPrice of this.backendSectors) {
            if (changeSectorPrice.colorSector === index) {
              changeSectorPrice.price = response.price;
            }
          }
        }
      });
    }

  }

  openDescription(): void {
    this.dialog.open(HelpDialogComponent, {
      // width: '50%',
      maxWidth: '1200px'
    });
  }

  dosmth2(e: Event): void {
    // has become redundant
    // e.stopPropagation();

  }

  createSector(): void {
    this.createNewSector = true;
  }

  closeSectorCreation(): void {
    this.createNewSector = false;
  }

  addSector(): void {
    const toAdd: SectorCreate = {
      id: this.startIndex,
      type: this.creationSectorType,
      price: this.creationSectorPrice,
      color: null,
      x: -1,
      y: -1,
      capacity: 10,
      width: -1,
      height: -1,
      rows: [],
      topLeft: undefined,
      colorSector: this.currentlySelectedSector
    };
    this.startIndex++;
    this.cancelCreatingSector();
    this.sectors.push(toAdd);
    const generatedColor = `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)},
     ${Math.floor(Math.random() * 255)}, `;
    // maybe check that the generated color is not to similar or similar to an already used color by comparing rgb values
    this.colors.push(generatedColor + '1)');
    this.colorsSector.push(generatedColor);

  }

  cancelCreatingSector(): void {
    this.creationSectorType = '';
    this.creationSectorPrice = null;
    this.createNewSector = false;
  }

  selectSector(index: number): void {
    this.currentlySelectedSector = index;
  }

  drawSeats(): void {

    if (this.redrawSeats) {
      this.places = [];
      for (let i = 0; i < this.placesToReDraw.length; i++) {
        this.places[i] = [];
      }


      for (let i = 0; i < this.placesToReDraw.length; i++) {
        for (const tempSeats of this.placesToReDraw[i]) {
          tempSeats.sectorId = -1;
          // maybe beSectorId auch auf -1 setzen
          this.places[i].push(tempSeats);
        }
      }

      return;
    }

    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;

    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    const numberOfPlacesInRow = Math.floor((this.svgWidth - (sidePadding + seatWidth * 2 + offsetX)) / ((seatWidth * 2) + offsetX));
    const numberOfRows = Math.floor((this.svgHeight - (sidePadding + 70)) / ((seathHeight * 2) + offsetY));


    let runningX = 100;
    let runningY = 120;

    const tempX = 15;
    let tempY = 120;
    for (let i = 0; i < numberOfRows; i++) {
      const rowNumbers = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      rowNumbers.setAttribute('x', tempX.toString());
      rowNumbers.setAttribute('y', tempY.toString());
      rowNumbers.setAttribute('id', `${tempX}, ${tempY}`);
      rowNumbers.setAttribute('class', 'rowEnum');
      rowNumbers.style.height = '24';
      rowNumbers.style.width = '8.6167';
      rowNumbers.innerHTML = (i + 1).toString();
      rowNumbers.style.userSelect = 'none';
      rowNumbers.style.pointerEvents = 'none';

      const canvasElement2 = document.getElementById('floorplan-canvas');
      canvasElement2.appendChild(rowNumbers);

      tempY = tempY + (seathHeight * 2 + offsetY);
    }

    for (let i = 0; i < numberOfRows; i++) {
      this.places[i] = [];
      runningX = 60;
      for (let j = 0; j < numberOfPlacesInRow; j++) {
        const x1 = runningX - seatWidth;
        const y1 = runningY - seathHeight;

        const x2 = runningX - seatWidth;
        const y2 = runningY + seathHeight;

        const x3 = runningX + seatWidth;
        const y3 = runningY + seathHeight;

        const x4 = runningX + seatWidth;
        const y4 = runningY - seathHeight;

        const x5 = runningX;
        const y5 = runningY - (seathHeight + seatLegspace);

        const addPlace: Place = {
          id: `${i},${j}`,
          sectorId: -1,
          beSectorId: -1,
          xCord: runningX,
          yCord: runningY,
          polygonPoints: `${x1} ${y1}\n${x2} ${y2}\n${x3} ${y3}\n${x4} ${y4}\n ${x5} ${y5}`,
          selected: false
        };

        this.places[i].push(addPlace);

        runningX = runningX + (seatWidth * 2 + offsetX);
      }
      runningY = runningY + (seathHeight * 2 + offsetY);
    }

    this.redrawSeats = false;
    this.places.pop();
  }

  createGrid(): void {

    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;

    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    const numberOfPlacesInRow = Math.floor((this.svgWidth - (sidePadding)) / ((seatWidth * 2) + offsetX));
    const numberOfRows = Math.floor((this.svgHeight - (sidePadding)) / ((seathHeight * 2) + offsetY));

    let runningX = 60;
    let runningY = 120;

    let tempRunningY = 120;

    for (let i = 0; i < numberOfRows; i++) {
        const x1 = runningX - seatWidth - offsetX / 2 - 30 ;
        const y1 = tempRunningY - seathHeight - seatLegspace - offsetY / 8 - 1.5;

        const gridElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElement.style.fill = 'gray';
        gridElement.setAttribute('cx', x1.toString());
        gridElement.setAttribute('cy', y1.toString());
        gridElement.setAttribute('r', '1');

        const addToGrid: GridElement = {
          x: x1,
          y: y1
        };

        this.grid.push(addToGrid);

        const elem = document.getElementById('floorplan-canvas');
        elem.appendChild(gridElement);

        tempRunningY = tempRunningY + (seathHeight * 2 + offsetY);
    }

    for (let i = 0; i < numberOfRows; i++) {
      this.places[i] = [];
      runningX = 60;
      for (let j = 0; j < numberOfPlacesInRow - 1; j++) {

        if (j === numberOfPlacesInRow - 2) {
          const extendedX = runningX + seatWidth + offsetX / 2 + 30;
          const extendedY = runningY - seathHeight - seatLegspace - offsetY / 8 - 1.5;
          const extendedGridElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
          extendedGridElement.style.fill = 'gray';
          extendedGridElement.setAttribute('cx', extendedX.toString());
          extendedGridElement.setAttribute('cy', extendedY.toString());
          extendedGridElement.setAttribute('r', '1');

          const extendedAddToGrid: GridElement = {
            x: extendedX,
            y: extendedY
          };

          this.grid.push(extendedAddToGrid);

          const elem2 = document.getElementById('floorplan-canvas');
          elem2.appendChild(extendedGridElement);
          continue;

        }

        const x1 = runningX + seatWidth + offsetX / 2 ;
        const y1 = runningY - seathHeight - seatLegspace - offsetY / 8 - 1.5;


        const gridElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElement.style.fill = 'gray';
        gridElement.setAttribute('cx', x1.toString());
        gridElement.setAttribute('cy', y1.toString());
        gridElement.setAttribute('r', '1');

        const addToGrid: GridElement = {
          x: x1,
          y: y1
        };

        this.grid.push(addToGrid);

        const elem = document.getElementById('floorplan-canvas');
        elem.appendChild(gridElement);
        runningX = runningX + (seatWidth * 2 + offsetX);
      }
      runningY = runningY + (seathHeight * 2 + offsetY);
    }
    runningX = 30;
  }

  checkThatAllSeatsAreInASector(): boolean {
    // checks that all seats have been assigned a sector
    // otherwise this layout must not be submitted
    for (const tempRow of this.places) {
      for (const tempSeat of tempRow) {
        if (tempSeat.sectorId === -1 && tempSeat.selected) {
          return false;
        }
      }
    }

    return true;
  }


  chooseSpot(id: string, e: Event): void {
    if (this.makeSectors) {
      return;
    }
    const indizes = id.split(',');
    const elem = document.getElementById(id);

    let isStandingSectorFlag = false;

    for (const runSector of this.sectors) {
      if (runSector.id === this.places[parseInt(indizes[0], 10)][parseInt(indizes[1], 10)].sectorId) {
        if (runSector.type === 'Standing') {
          return;
        }
      }
    }


    // schauen ob der platz zu einem sektor gehört, dann in dem array vom sektor das auch updaten
    //  also die value von chosen
    let seatFound = false;
    for (let i = 0; i < this.backendSectors.length && !seatFound; i++) {
      for (let j = 0; j < this.backendSectors[i].rows.length && !seatFound; j++) {
        for (let k = 0; k < this.backendSectors[i].rows[j].seats.length && !seatFound; k++) {
          if (this.backendSectors[i].rows[j].seats[k].seatId === id) {
            seatFound = true;


            if (this.backendSectors[i].type === 'Standing') {
              isStandingSectorFlag = true;
              break;
            }

            if (this.places[parseInt(indizes[0], 10)][parseInt(indizes[1], 10)].selected) {
              this.backendSectors[i].rows[j].seats[k].chosen = false;
            } else {
              this.backendSectors[i].rows[j].seats[k].chosen = true;
            }
          }

        }
      }
    }


    if (isStandingSectorFlag) {
      e.stopPropagation();
      return;
    }

    if (this.places[parseInt(indizes[0], 10)][parseInt(indizes[1], 10)].selected) {
      this.places[parseInt(indizes[0], 10)][parseInt(indizes[1], 10)].selected = false;
      elem.style.fill = 'white';

    } else {
      this.places[parseInt(indizes[0], 10)][parseInt(indizes[1], 10)].selected = true;
      elem.style.fill = 'lightgray';

    }
    e.stopPropagation();
  }

  markAllSpotsAsChosen(): void {


    for (const tempBackSector of this.backendSectors) {
      for (const tempBackSectorRow of tempBackSector.rows) {
        for (const tempBackSectorRowSeat of tempBackSectorRow.seats) {
          tempBackSectorRowSeat.chosen = true;
        }
      }
    }

    for (const tempRow of this.places) {
      for (const tempSeat of tempRow) {
        if (this.isStandingSector(tempSeat.sectorId)) {
          continue;
        }
        tempSeat.selected = true;
        const elem = document.getElementById(tempSeat.id);
        elem.style.fill = 'lightgray';
      }
    }
  }

  markAllSpotsAsFree(): void {

    for (const tempBackSector of this.backendSectors) {
      for (const tempBackSectorRow of tempBackSector.rows) {
        for (const tempBackSectorRowSeat of tempBackSectorRow.seats) {
          tempBackSectorRowSeat.chosen = false;
        }
      }
    }

    for (const tempRow of this.places) {
      for (const tempSeat of tempRow) {
        tempSeat.selected = false;
        const elem = document.getElementById(tempSeat.id);
        elem.style.fill = 'white';
      }
    }
  }

  markAllSpotsInSpecifiedSector(sectorId: number) {
    for (let i = 0; i < this.places.length; i++) {
      for (let j = 0; j < this.places[i].length; j++) {
        if (this.places[i][j].sectorId === sectorId) {
          this.places[i][j].selected = true;
          const elem = document.getElementById(`${i},${j}`);
          elem.style.fill = 'lightgray';
        }
      }
    }
  }

  showColor(event: Event): void {
    event.stopPropagation();
  }

  closeColorPicker(event: Event): void {
    const doc = document.getElementById('expansionPanel');
    doc.style.overflow = 'hidden';


    const changeSectorsColors = document.getElementsByClassName(`sector-${this.currentlySelectedSector}`);
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < changeSectorsColors.length; i++) {
      (changeSectorsColors[i] as HTMLElement).style.fill = this.colors[this.currentlySelectedSector];
      (changeSectorsColors[i] as HTMLElement).style.stroke = this.colors[this.currentlySelectedSector];
    }

    for (const changeSectorColor of this.backendSectors) {
      if (changeSectorColor.colorSector === this.currentlySelectedSector) {
        changeSectorColor.color = this.colors[this.currentlySelectedSector];
      }
    }

  }

  openColorPicker(): void {
    const doc = document.getElementById('expansionPanel');
    doc.style.overflow = 'visible';
  }


  flipped(): void {
    if (!this.makeSectors) {
      const svg = document.querySelector('svg');
      const rectangles = svg.querySelectorAll('rect');

      // eslint-disable-next-line @typescript-eslint/prefer-for-of
      for (let i = 0; i < rectangles.length; i++) {
        rectangles[i].style.pointerEvents = 'all';
        rectangles[i].style.userSelect = 'all';
      }


      for (const tempRow of this.places) {
        for (const tempSeat of tempRow) {
          const elem = document.getElementById(tempSeat.id);
          elem.style.userSelect = 'none';
          elem.style.pointerEvents = 'none';
        }
      }
    } else {
      const svg = document.querySelector('svg');
      const rectangles = svg.querySelectorAll('rect');

      // eslint-disable-next-line @typescript-eslint/prefer-for-of
      for (let i = 0; i < rectangles.length; i++) {
        rectangles[i].style.pointerEvents = 'none';
        rectangles[i].style.userSelect = 'none';
      }

      for (const tempRow of this.places) {
        for (const tempSeat of tempRow) {
          const elem = document.getElementById(tempSeat.id);
          elem.style.userSelect = 'all';
          elem.style.pointerEvents = 'all';
        }
      }
    }
    this.makeSectors = ! this.makeSectors;
  }


  checkIfVertexInsideOfSector(x: number, y: number, a: number, b: number, top: number, bottom: number): boolean {
    return ( (x > a && x < b) && (y > top && y < bottom ) );
  }


  removeAllSectors(): void {
    const sectorsToBeRemoved = document.getElementsByClassName('sector');
    for (let i = sectorsToBeRemoved.length - 1; i >= 0; --i) {
      sectorsToBeRemoved[i].remove();
    }

    for (let i = 0; i < this.places.length; i++) {
      this.placesToReDraw[i] = new Array(this.places[i].length).fill(0);
    }

    for (let i = 0; i < this.places.length; i++) {
      for (let j = this.places[i].length - 1; j >= 0; --j) {
        this.placesToReDraw[i][j] = this.places[i][j];

        const seatToBeRemoved = document.getElementById(this.places[i][j].id);
        seatToBeRemoved.remove();
      }
    }

    this.places = [];

    this.backendSectors = [];
    this.redrawSeats = true;
    this.createGrid();
    this.drawSeats();
  }

  drop(e: Event): void {
    e.stopPropagation();
  }


  centerScene(): void {
    const scene = document.getElementById('scene');
    const centerPoint = this.svgWidth;
    const newWidth = this.svgWidth * 0.5;
    scene.setAttribute('width', (newWidth).toString());
    const newStart = (this.svgWidth - newWidth) / 2;
    scene.setAttribute('x', newStart.toString());

    const sceneText = document.getElementById('scene-text');
    sceneText.setAttribute('x', (centerPoint - 100 + 70).toString());

  }


  expandGridHorizontally(): void {
    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;

    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    const runningY = 120;



    // remove outermost snapping points
    let maxXinGrid = -1;
    for (const findMaxInGrid of this.grid) {
      maxXinGrid = (findMaxInGrid.x > maxXinGrid) ? findMaxInGrid.x : maxXinGrid;
    }

    const removeSnappingPointFromCanvas = document.getElementsByTagNameNS('http://www.w3.org/2000/svg', 'circle');

    for (let i = this.grid.length - 1; i >= 0; i--) {
      if (this.grid[i].x === maxXinGrid) {
        // is not iterable
        // eslint-disable-next-line @typescript-eslint/prefer-for-of
        for (let j = 0; j < removeSnappingPointFromCanvas.length; j++) {
          if (removeSnappingPointFromCanvas[j].getAttribute('cx') === this.grid[i].x.toString()
            && removeSnappingPointFromCanvas[j].getAttribute('cy') === this.grid[i].y.toString()) {
              removeSnappingPointFromCanvas[j].remove();
              break;
            }
        }
        this.grid.splice(i, 1);
      }
    }

    for (let i = 0; i < this.places.length; i++) {
      const neighborSeat = this.places[i][this.places[i].length - 1];

      const newSeatX = neighborSeat.xCord; // + (seatWidth * 2 + offsetX);
      const newSeatY = neighborSeat.yCord; // + (seathHeight * 2 + offsetY);

      const leftX = newSeatX + seatWidth + offsetX / 2;
      const rightX = newSeatX + (seatWidth * 2 + offsetX) + seatWidth + offsetX / 2 + 30;
      const y = newSeatY - seathHeight - seatLegspace - offsetY / 8 - 1.5;

      const gridElementLeft = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      gridElementLeft.style.fill = 'gray';
      gridElementLeft.setAttribute('cx', leftX.toString());
      gridElementLeft.setAttribute('cy', y.toString());
      gridElementLeft.setAttribute('r', '1');

      const gridElementRight = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      gridElementRight.style.fill = 'gray';
      gridElementRight.setAttribute('cx', rightX.toString());
      gridElementRight.setAttribute('cy', y.toString());
      gridElementRight.setAttribute('r', '1');

      const addToGridLeft: GridElement = {
        x: leftX,
        y
      };
      const addToGridRight: GridElement = {
        x: rightX,
        y
      };

      this.grid.push(addToGridLeft);
      this.grid.push(addToGridRight);
      const elem = document.getElementById('floorplan-canvas');
      elem.appendChild(gridElementLeft);
      elem.appendChild(gridElementRight);

      if (i === this.places.length - 1) {

        const lowerY = newSeatY + seathHeight + offsetY / 8 + 1.5;
        const gridElementLeftBottom = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElementLeftBottom.style.fill = 'gray';
        gridElementLeftBottom.setAttribute('cx', leftX.toString());
        gridElementLeftBottom.setAttribute('cy', lowerY.toString());
        gridElementLeftBottom.setAttribute('r', '1');

        const gridElementRightBottom = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElementRightBottom.style.fill = 'gray';
        gridElementRightBottom.setAttribute('cx', rightX.toString());
        gridElementRightBottom.setAttribute('cy', lowerY.toString());
        gridElementRightBottom.setAttribute('r', '1');

        const addToGridLeftBottom: GridElement = {
          x: leftX,
          y: lowerY
        };
        const addToGridRightBottom: GridElement = {
          x: rightX,
          y: lowerY
        };

        this.grid.push(addToGridLeftBottom);
        this.grid.push(addToGridRightBottom);
        elem.appendChild(gridElementLeftBottom);
        elem.appendChild(gridElementRightBottom);

      }
    }

  }

  expandGridVertically(): void {
    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;

    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    for (let i = 0; i < this.places[0].length; i++) {
      const x1 = this.places[0][i].xCord + seatWidth + offsetX / 2;
      const y1 = this.places[this.places.length - 1][0].yCord + seathHeight + offsetY / 8 + 1.5 + (seathHeight * 2 + offsetY);

      if (i === 0) {
        const x2 = this.places[0][i].xCord - seatWidth - offsetX / 2  - 30;
        const y2 = this.places[this.places.length - 1][0].yCord + seathHeight + offsetY / 8 + 1.5 + (seathHeight * 2 + offsetY);

        const gridElement2 = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElement2.style.fill = 'gray';
        gridElement2.setAttribute('cx', x2.toString());
        gridElement2.setAttribute('cy', y2.toString());
        gridElement2.setAttribute('r', '1');

        const addToGrid2: GridElement = {
          x: x2,
          y: y2
        };

        this.grid.push(addToGrid2);

        const elem2 = document.getElementById('floorplan-canvas');
        elem2.appendChild(gridElement2);

      }

      if (i === this.places[0].length - 1) {
        const x3 = this.places[0][i].xCord + seatWidth + offsetX / 2  + 30;
        const y3 = this.places[this.places.length - 1][0].yCord + seathHeight + offsetY / 8 + 1.5 + (seathHeight * 2 + offsetY);

        const gridElement3 = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElement3.style.fill = 'gray';
        gridElement3.setAttribute('cx', x3.toString());
        gridElement3.setAttribute('cy', y3.toString());
        gridElement3.setAttribute('r', '1');

        const addToGrid2: GridElement = {
          x: x3,
          y: y3
        };

        this.grid.push(addToGrid2);

        const elem2 = document.getElementById('floorplan-canvas');
        elem2.appendChild(gridElement3);
        break;
      }

      const gridElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElement.style.fill = 'gray';
        gridElement.setAttribute('cx', x1.toString());
        gridElement.setAttribute('cy', y1.toString());
        gridElement.setAttribute('r', '1');

        const addToGrid: GridElement = {
          x: x1,
          y: y1
        };

        this.grid.push(addToGrid);

        const elem = document.getElementById('floorplan-canvas');
        elem.appendChild(gridElement);
    }

  }


  addColumn(): void {


    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;
    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    if (this.inputEventHall !== undefined) {
      if ((this.svgWidth + (seatWidth * 2 + offsetX) > this.parentEventHall.geometry.width)) {
        this.snackbar.open('Error: Layout cant be bigger than eventhall', 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
        return;
      }
    }

    this.expandGridHorizontally();



    for (const sector of this.backendSectors) {
      let snapPointExists = false;
      for (const gridEl of this.grid) {
        if (gridEl.x === (sector.topLeft.x + sector.width) && gridEl.y === (sector.topLeft.y + sector.height)) {
          snapPointExists = true;
          break;
        }
      }

      if (!snapPointExists) {
        // search next nearest snap point
        let nearestSnap;
        let minDist = Number.MAX_VALUE;
        const topRight = sector.topLeft.x + sector.width;
        for (const newSnapPoint of this.grid) {
          if ( (topRight - newSnapPoint.x >= 0) && (topRight - newSnapPoint.x <= minDist) ) {
            minDist = topRight - newSnapPoint.x;
            nearestSnap = newSnapPoint;
          }
        }
        sector.width = nearestSnap.x - sector.topLeft.x;
        const changeSectorWidth = document.getElementById(sector.id.toString());
        changeSectorWidth.setAttribute('width', sector.width.toString());
      }
    }


    this.svgWidth = this.svgWidth + (seatWidth * 2 + offsetX);

    for (let i = 0; i < this.places.length; i++) {
      if (this.places[i].length === 0) {
        continue;
      }

      const neighborSeat = this.places[i][this.places[i].length - 1];
      const nextX = neighborSeat.xCord;
      const nextY = neighborSeat.yCord;

      const currX = nextX + 30 + (seatWidth * 2 + offsetX) - 30;
      const currY = nextY;

      const x1 = currX - seatWidth;
      const y1 = currY - seathHeight;

      const x2 = currX - seatWidth;
      const y2 = currY + seathHeight;

      const x3 = currX + seatWidth;
      const y3 = currY + seathHeight;

      const x4 = currX + seatWidth;
      const y4 = currY - seathHeight;

      const x5 = currX;
      const y5 = currY - (seathHeight + seatLegspace);

      const addPlace: Place = {
        id: `${i},${this.places[i].length}`,
        sectorId: -1,
        beSectorId: -1,
        xCord: currX,
        yCord: currY,
        polygonPoints: `${x1} ${y1}\n${x2} ${y2}\n${x3} ${y3}\n${x4} ${y4}\n ${x5} ${y5}`,
        selected: false
      };

      this.places[i].push(addPlace);
    }
    this.centerScene();
  }

  filterGrid(val: number): void {
    for (let i = this.grid.length - 1; i >= 0; i--) {
      if (this.grid[i].x === val) {
        this.grid.splice(i, 1);
      }
    }
  }

  removeRightFromGrid(val: number): void {
    const removeOuterMostGridElement = document.getElementsByTagNameNS('http://www.w3.org/2000/svg', 'circle');

    for (let i = this.grid.length - 1; i >= 0; i--) {
      if (this.grid[i].x === val) {
        for (let j = removeOuterMostGridElement.length - 1; j >= 0; j--) {

          if (removeOuterMostGridElement[j].getAttribute('cx') === this.grid[i].x.toString()) {
              removeOuterMostGridElement[j].remove();
              break;
            }
        }
      }
    }

  }

  removeColumn(): void {

    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;

    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    if (this.places[0].length === 1) {
      this.snackbar.open('Min number of columns is 1', 'Dismiss', {
        duration: 1300,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['snack-error']
      });
      return;
    }

    for (const tempRow of this.places) {
      if (tempRow[tempRow.length - 1].sectorId !== -1
        || tempRow[tempRow.length - 1].beSectorId !== -1) {
          this.snackbar.open('Cant delete column as it contains a sector', 'Dismiss', {
            duration: 1300,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
          return;
        }
    }
    let maxXvalue = -1;
    for (const findMaxInGrid of this.grid) {
      maxXvalue = (findMaxInGrid.x > maxXvalue) ? findMaxInGrid.x : maxXvalue;
    }

    this.removeRightFromGrid(maxXvalue);
    this.filterGrid(maxXvalue);

    maxXvalue = -1;
    for (const findMaxInGrid of this.grid) {
      maxXvalue = (findMaxInGrid.x > maxXvalue) ? findMaxInGrid.x : maxXvalue;
    }

    this.removeRightFromGrid(maxXvalue);
    this.filterGrid(maxXvalue);


    const toRemoveSpots = document.getElementsByClassName('spot');
    for (let i = toRemoveSpots.length - 1; i >= 0; i--) {
      if (parseInt(toRemoveSpots[i].id.split(',')[1], 10) === (this.places[0].length - 1)) {
        toRemoveSpots[i].remove();
      }
    }


    for (const removePlace of this.places) {
      removePlace.pop();
    }


    for (let i = 0; i < this.places.length; i++) {
      const neighborSeat = this.places[i][this.places[i].length - 1];

      const newSeatX = neighborSeat.xCord; // + (seatWidth * 2 + offsetX);
      const newSeatY = neighborSeat.yCord; // + (seathHeight * 2 + offsetY);

      const leftX = newSeatX + seatWidth + offsetX / 2 + 30;
      const rightX = newSeatX + (seatWidth * 2 + offsetX) + seatWidth + offsetX / 2 + 30;
      const y = newSeatY - seathHeight - seatLegspace - offsetY / 8 - 1.5;

      const gridElementLeft = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      gridElementLeft.style.fill = 'gray';
      gridElementLeft.setAttribute('cx', leftX.toString());
      gridElementLeft.setAttribute('cy', y.toString());
      gridElementLeft.setAttribute('r', '1');

      const gridElementRight = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      gridElementRight.style.fill = 'gray';
      gridElementRight.setAttribute('cx', rightX.toString());
      gridElementRight.setAttribute('cy', y.toString());
      gridElementRight.setAttribute('r', '20');

      const addToGridLeft: GridElement = {
        x: leftX,
        y
      };
      const addToGridRight: GridElement = {
        x: rightX,
        y
      };

      this.grid.push(addToGridLeft);
      const elem = document.getElementById('floorplan-canvas');
      elem.appendChild(gridElementLeft);

      if (i === this.places.length - 1) {

        const lowerY = newSeatY + seathHeight + offsetY / 8 + 1.5;
        const gridElementLeftBottom = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElementLeftBottom.style.fill = 'gray';
        gridElementLeftBottom.setAttribute('cx', leftX.toString());
        gridElementLeftBottom.setAttribute('cy', lowerY.toString());
        gridElementLeftBottom.setAttribute('r', '1');

        const gridElementRightBottom = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        gridElementRightBottom.style.fill = 'gray';
        gridElementRightBottom.setAttribute('cx', rightX.toString());
        gridElementRightBottom.setAttribute('cy', lowerY.toString());
        gridElementRightBottom.setAttribute('r', '1');

        const addToGridLeftBottom: GridElement = {
          x: leftX,
          y: lowerY
        };
        const addToGridRightBottom: GridElement = {
          x: rightX,
          y: lowerY
        };

        this.grid.push(addToGridLeftBottom);
        elem.appendChild(gridElementLeftBottom);

      }
    }


    this.svgWidth = this.svgWidth - (seatWidth * 2 + offsetX);
    this.svgWidth = this.svgWidth;
    this.centerScene();
  }


  addRow(): void {
    const seathHeight = 30 / 2;
    const seatWidth = 30 / 2;
    const seatLegspace = 12;

    const sidePadding = 20;
    const offsetX = 10;
    const offsetY = 20;

    // We add a layout to an eventhall, so the size of the layout must not exceed the one of the eventhall
    if (this.inputEventHall !== undefined) {
      if ((this.svgHeight + (seathHeight * 2 + offsetY)) > this.parentEventHall.geometry.height) {
        this.snackbar.open('Error: Layout cant be bigger than eventhall', 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
        return;
      }
    }


    this.expandGridVertically();



    this.svgHeight = this.svgHeight + (seathHeight * 2 + offsetY);


    const rowToBeAdded = [];

    let currX = 60;

    const insertNumberY = this.places[this.places.length - 1][0].yCord + (seathHeight * 2 + offsetY);
    const insertNumberX = 15;

    const rowNumbers = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    rowNumbers.setAttribute('x', insertNumberX.toString());
    rowNumbers.setAttribute('y', insertNumberY.toString());
    rowNumbers.setAttribute('id', `${insertNumberX}, ${insertNumberY}`);
    rowNumbers.setAttribute('class', 'rowEnum');
    rowNumbers.style.height = '24';
    rowNumbers.style.width = '8.6167';
    rowNumbers.innerHTML = (this.places.length + 1).toString();
    rowNumbers.style.userSelect = 'none';
    rowNumbers.style.pointerEvents = 'none';

    const canvasElement2 = document.getElementById('floorplan-canvas');
    canvasElement2.appendChild(rowNumbers);



    for (let i = 0; i < this.places[0].length; i++) {
      const currY = this.places[this.places.length - 1][0].yCord + (seathHeight * 2 + offsetY);

      const x1 = currX - seatWidth;
      const y1 = currY - seathHeight;

      const x2 = currX - seatWidth;
      const y2 = currY + seathHeight;

      const x3 = currX + seatWidth;
      const y3 = currY + seathHeight;

      const x4 = currX + seatWidth;
      const y4 = currY - seathHeight;

      const x5 = currX;
      const y5 = currY - (seathHeight + seatLegspace);

      const addPlace: Place = {
        id: `${this.places.length},${i}`,
        sectorId: -1,
        beSectorId: -1,
        xCord: currX,
        yCord: currY,
        polygonPoints: `${x1} ${y1}\n${x2} ${y2}\n${x3} ${y3}\n${x4} ${y4}\n ${x5} ${y5}`,
        selected: false
      };

      rowToBeAdded.push(addPlace);
      currX = currX + (seatWidth * 2 + offsetX);
    }
    this.places.push(rowToBeAdded);
  }

  removeRow(): void {
    const seathHeight = 30 / 2;
    const offsetY = 20;

    if (this.places.length === 1) {
      this.snackbar.open('Min number of rows is 1', 'Dismiss', {
        duration: 1300,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['snack-error']
      });
      return;
    }

    const referenceHeight = this.places[this.places.length - 1][0].yCord;

    for (const checkSectorBounds of this.backendSectors) {
      if ( (referenceHeight <= (checkSectorBounds.y + (checkSectorBounds.height / 2)) )
          && (referenceHeight >= (checkSectorBounds.y - (checkSectorBounds.height / 2))) ) {
            this.snackbar.open('Cant delete row as it contains a sector', 'Dismiss', {
              duration: 1300,
              horizontalPosition: 'right',
              verticalPosition: 'top',
              panelClass: ['snack-error']
            });
            return;
      }
    }

    const enumerateRowsToRemove = document.getElementsByClassName('rowEnum');
    // is not iterable
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < enumerateRowsToRemove.length; i++) {
      if (enumerateRowsToRemove[i].innerHTML === (this.places.length.toString())) {
        enumerateRowsToRemove[i].remove();
        break;
      }
    }

    const toRemoveSpots = document.getElementsByClassName('spot');
    for (let i = toRemoveSpots.length - 1; i >= 0; i--) {
      if (parseInt(toRemoveSpots[i].id.split(',')[0], 10) === (this.places.length - 1)) {
        toRemoveSpots[i].remove();
      }
    }

    this.places.pop();
    this.svgHeight = this.svgHeight - (seathHeight * 2 + offsetY);

  }

  clickOnScene(event: Event): void {
    event.stopPropagation();
  }


  checkIfLayoutNameExists(): void {
    this.layoutService.getAllByName(this.layoutName).subscribe({
      next: data => {
        if (data.length > 0) {
          this.snackbar.open('Error: A layout with this name already exists', 'Dismiss', {
            duration: 1300,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
        });
          return;
        }

        this.submitLayout();
      },
      error: error => {
        this.snackbar.open(`${error.error.error}`, 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
        return;
      }
    });
  }

  submitLayout(): void {

    // check if there are seats that are not in any sector and that are selected
    for (const row of this.places) {
      for (const seat of row) {
        if ((seat.beSectorId === -1 || seat.sectorId === -1) && (seat.selected)) {
          this.snackbar.open('Every chosen seat must be in a sector', 'Dismiss', {
            duration: 1300,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
          return;
        }
      }
    }

    if (this.backendSectors.length === 0) {
      this.snackbar.open('Error: No sectors chosen', 'Dismiss', {
        duration: 1300,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['snack-error']
      });
      return;
    }


    // for every sector, check that it is not empty, meaning it contains atleast one seat
    for (const sector of this.backendSectors) {
      if (sector.type === 'Standing') {
        continue;
      }
      let containsSeat = false;
      for (const row1 of this.places) {
        for (const seat1 of row1) {
          if (seat1.selected && (seat1.beSectorId === sector.id)) {
            containsSeat = true;
            break;
          }
        }
        if (containsSeat) {
          break;
        }
      }
      if (!containsSeat) {
        this.snackbar.open('Error: Sector must contain atleast one selected seat', 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
        return;
      }
    }


    // remove seats that are not selected from sector
    // remove rows in which there are not seats.
    for (let i = this.backendSectors.length - 1; i >= 0; i--) {
      if (this.backendSectors[i].type === 'Standing') {
        continue;
      }
      const anyRow = false;
      for (let j = this.backendSectors[i].rows.length - 1; j >= 0; j--) {
        let anySeats = false;
        for (let k = this.backendSectors[i].rows[j].seats.length - 1; k >= 0; k--) {
          if (this.backendSectors[i].rows[j].seats[k].chosen === false) {
            this.backendSectors[i].rows[j].seats.splice(k, 1);
          } else {
            anySeats = true;
          }
        }
        if (anySeats === false) {
          this.backendSectors[i].rows.splice(j, 1);
        }
      }
    }


    const rowSector: SectorCreate = {
      id: this.enumerate + 1,
      type: 'Seating',
      price: -1,
      capacity: -1,
      x: -1,
      y: -1,
      height: 0,
      width: 0,
      rows: [],
      topLeft: {
        x: 0,
        y: 0,
      },
      colorSector: -1,
      color: 'white'
    };

    let rowCounter = 1;
    const enumerationRows: RowCreate[] = [];

    const rowEnumerations = document.getElementsByClassName('rowEnum');

    for (const enumRow of this.places) {
      let atleasteOneStanding = false;
      let emptyRow = true;
      let onlyStandingsInRow = true;
      let containsSeat = false;
      let placeIsChosenOrStandingSector = false;
      for (const enumSeat of enumRow) {
        if (!atleasteOneStanding) {
          atleasteOneStanding = this.isStandingSector(enumSeat.sectorId);
        }
        // check whether there are only standings in this row
        onlyStandingsInRow = onlyStandingsInRow && (this.isStandingSector(enumSeat.sectorId)  || (enumSeat.selected === false));
        emptyRow = emptyRow && (enumSeat.selected === false && !(this.isStandingSector(enumSeat.sectorId)));

        containsSeat = (this.isSeatingSector(enumSeat.sectorId)) ? true : containsSeat;

        // if (chosen and seating sector) or (standing) then we add anumber
        if ((enumSeat.selected && this.isSeatingSector(enumSeat.sectorId)) || (this.isStandingSector(enumSeat.sectorId))) {
          placeIsChosenOrStandingSector = true;
        }
      }
      if (emptyRow) {

        continue;
      }

      // Can be used to to not enumerate row that only contain standing seats
      if (onlyStandingsInRow && atleasteOneStanding) {
        continue;
      }

      // if chosenorstandingsector then we want to enumerate the row
      if (placeIsChosenOrStandingSector) {
        // Create row with enumeration and coordiantes and push it so sector.rows array
        const addEnumRow: RowCreate = {
          number: rowCounter,
          seats: [],
          x: 10,
          y: 5000
        };
        rowCounter++;
        // is not iterable
        // eslint-disable-next-line @typescript-eslint/prefer-for-of
        for (let i = 0; i < rowEnumerations.length; i++) {
          if (enumRow[0].yCord === parseInt(rowEnumerations[i].id.split(',')[1], 10)) {
            addEnumRow.x = parseInt(rowEnumerations[i].id.split(',')[0], 10) - 9.31668;
            addEnumRow.y = parseInt(rowEnumerations[i].id.split(',')[1], 10) - 21.6666;
            break;
          }
        }
        enumerationRows.push(addEnumRow);
      }
    }

    rowSector.rows = enumerationRows;


    for (const backendSector of this.backendSectors) {
      backendSector.x = backendSector.topLeft.x;
      backendSector.y = backendSector.topLeft.y;
    }


  const eventHallId = (this.inputEventHall === undefined) ? -1 : this.inputEventHall;




  // store proper row number in every row in every sector
  for (const changeSectorEnum of this.backendSectors) {
    if (changeSectorEnum.type === 'Standing') {
      continue;
    }
    for (const everyRow of changeSectorEnum.rows) {
        for (const inspectRow of enumerationRows) {
          if ((everyRow.seats[0].y - 21.6666) === inspectRow.y) {
            everyRow.number = inspectRow.number;
          }
        }
    }
  }

  const addLayout: CreateLayout = {
    name: this.layoutName,
    sectors: this.backendSectors
  };

  const addEventHall: CreateEventHall = {
    id: eventHallId,
    name: this.generalEventHallInformation.name,
    location: this.generalEventHallInformation.location,
    x: -1,
    y: -1,
    width: this.svgWidth,
    height: this.svgHeight,
    layout: addLayout
  };

  addEventHall.layout.sectors.push(rowSector);

    if (this.inputEventHall === undefined) {
      this.eventHallService.createEventHall(addEventHall).subscribe({
        next: data => {
          this.snackbar.open('Successfully created eventhall', 'Dismiss', {
            duration: 1300,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-success']
          });
          this.router.navigate(['/performance/create']);
        },
        error: error => {
          this.snackbar.open(`${error.error.error}`, 'Dissmiss', {
            duration: 1500,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
        }
      });
      return;
    }

    // if we add a layout to an existing eventhall
    this.eventHallService.addLayoutToEventHall(addEventHall).subscribe({
      next: data => {
        this.snackbar.open('Successfully added layout to eventhall', 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.router.navigate(['/performance/create']);
      },
      error: error => {
        this.snackbar.open(`${error.error.error}`, 'Dismiss', {
          duration: 1500,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });

    return;
  }

}
