import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Standing} from '../../../../dtos/standing';
import {MatDialog} from '@angular/material/dialog';
import {DialogChooseSpotsComponent} from './dialog-choose-spots/dialog-choose-spots.component';
import {TicketStatus} from '../../../../enums/ticket-status';
import {Ticket} from '../../../../dtos/ticket';
import { Observable } from 'rxjs';
import { Sector } from 'src/app/dtos/sector';

@Component({
  selector: 'app-standing',
  templateUrl: './standing.component.html',
  styleUrls: ['./standing.component.scss']
})
export class StandingComponent implements OnInit {

  @Input() getUpdatedSector: Observable<Sector>;
  @Input() standing: Standing;
  @Output() chosenTicketsEvent = new EventEmitter<Ticket[]>();
  chosenTickets: Ticket[];
  freeSpots: number;
  reservedSpots: number;
  purchasedSpots: number;

  constructor(public dialog: MatDialog) {
    this.chosenTickets = [];
    this.reservedSpots = 0;
    this.purchasedSpots = 0;
  }

  ngOnInit(): void {
    this.getUpdatedSector.subscribe((data) => this.updateSectorAndContainingSpots(data));
    this.updateSpots();
    this.chosenTicketsEvent.emit(this.chosenTickets);
  }

  updateSpots() {
    let reservedSpots = 0;
    let purchasedSpots = 0;
    this.chosenTickets = [];

    this.standing.stands.forEach((stand) => {
      if (stand.chosen) {
        this.chosenTickets.push(stand.ticket);
      } else if (stand.ticket.ticketStatus === TicketStatus.reserved) {
        reservedSpots++;
      } else if (stand.ticket.ticketStatus === TicketStatus.purchased) {
        purchasedSpots++;
      }
    });
    this.reservedSpots = reservedSpots;
    this.purchasedSpots = purchasedSpots;
    this.freeSpots = this.standing.capacity - (reservedSpots + purchasedSpots);
  }

  selectSpot() {
    const dialogRef = this.dialog.open(DialogChooseSpotsComponent, {
      data: {sectionName: this.standing.sectorId, freeSpots: this.freeSpots, selectedSpots: this.chosenTickets.length},
    });

    dialogRef.afterClosed().subscribe(result => {
      this.chooseSpot(result);
    });
  }

  removeSelection(numberOfSpotsToRemove: number) {
    this.standing.stands.every((stand) => {
      if (stand.ticket.ticketStatus === TicketStatus.free && stand.chosen === true) {
        stand.chosen = false;
        numberOfSpotsToRemove--;
      }
      return numberOfSpotsToRemove > 0;

    });
    this.chosenTicketsEvent.emit([]);
    this.chosenTickets = [];
    this.updateSpots();
  }

  chooseSpot(selectedSpots: number) {
    let leftToChoose = selectedSpots - this.chosenTickets.length;
    if (leftToChoose < 0) {
      this.removeSelection(-leftToChoose);
    } else if (leftToChoose !== 0) {
      this.standing.stands.every((stand) => {
        if (stand.ticket.ticketStatus === TicketStatus.free && (stand.chosen === false || stand.chosen === undefined)) {
          stand.chosen = true;
          this.chosenTickets.push(stand.ticket);
          leftToChoose--;
        }
        return leftToChoose > 0;
      });
    }
    this.chosenTicketsEvent.emit(this.chosenTickets);
    this.updateSpots();
  }

  updateSectorAndContainingSpots(sector: any): void {
    let countHowManyAreNowTaken = 0;
    let countReserved = 0;
    let countPurchased = 0;
    const temp: Standing = sector;
    for (const stand of temp.stands) {
      if (stand.ticket.ticketStatus === TicketStatus.reserved) {
        countReserved++;
      } else if (stand.ticket.ticketStatus === TicketStatus.purchased) {
        countPurchased++;
      }
    }

    countHowManyAreNowTaken = countReserved + countPurchased;
    this.reservedSpots = countReserved;
    this.purchasedSpots = countPurchased;
    this.freeSpots = temp.stands.length - countHowManyAreNowTaken;

    const totalNumberOfStands = temp.stands.length;
    const stillAvailable = totalNumberOfStands - countHowManyAreNowTaken;


    if (stillAvailable === 0) {
      this.chosenTickets = [];
      this.chosenTicketsEvent.emit(this.chosenTickets);
    }

    if (stillAvailable < this.chosenTickets.length) {
      this.chosenTickets = [];
      this.chosenTicketsEvent.emit(this.chosenTickets);
    }
  }
}
