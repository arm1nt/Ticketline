import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Sector} from '../../../dtos/sector';
import {Standing} from '../../../dtos/standing';
import {Seating} from '../../../dtos/seating';
import {Stand} from '../../../dtos/stand';
import {Seat} from '../../../dtos/seat';
import {Ticket} from '../../../dtos/ticket';
import { Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-sector',
  templateUrl: './sector.component.html',
  styleUrls: ['./sector.component.scss']
})
export class SectorComponent implements OnInit {

  @Input() sector: Sector;
  @Input() updatedSector: Observable<Sector>;
  @Output() chosenTicketsEvent = new EventEmitter<Ticket[]>();
  informChildSector: Subject<Sector> = new Subject<Sector>();

  constructor() { }

  ngOnInit(): void {
    this.updatedSector.subscribe((data) => this.updateSectorAndSpots(data));
  }

  isStanding(): boolean {
    return this.sector instanceof Standing;
  }
  isSeating(): boolean {
    return this.sector instanceof Seating;
  }
  getStanding(): Standing {
    if (this.isStanding()) {
      return this.sector as Standing;
    }
    return null;
  }
  getSeating(): Seating {
    if (this.isSeating()) {
      return this.sector as Seating;
    }
  }

  choseTickets(tickets: Ticket[]) {
    this.chosenTicketsEvent.emit(tickets);
  }

  updateSectorAndSpots(tempSec: Sector): void {
    if (tempSec.id === this.sector.id) {
      this.informChildSector.next(tempSec);
    }


  }

}
