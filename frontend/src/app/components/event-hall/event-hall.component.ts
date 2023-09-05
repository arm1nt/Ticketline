import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Layout} from '../../dtos/layout';
import {Sector} from '../../dtos/sector';
import {Ticket} from '../../dtos/ticket';
import {PerformanceService} from '../../services/performance.service';
import {Observable, Subject} from 'rxjs';

@Component({
  selector: 'app-event-hall',
  templateUrl: './event-hall.html',
  styleUrls: ['./event-hall.scss']
})
export class EventHallComponent implements OnInit {

  @Input() layout: Layout;
  @Input() performanceId: number;
  @Input() checkSelectedSeatsAndSelectedTickets: Observable<void>;
  @Output() chosenTicketsEvent = new EventEmitter<Ticket[]>();
  @Output() sectorsWithTicketsEvent? = new EventEmitter<Map<Sector, Ticket[]>>();

  informSectors: Subject<Sector> = new Subject<Sector>();
  sceneX = 1;
  sceneWidth = 10;
  refreshed = 0;


  public chosenTickets: Map<string, Ticket[]>;
  public chosenTicketsForBackendSectors: Map<Sector, Ticket[]>;

  constructor(private performanceService: PerformanceService) {
  }

  ngOnInit(): void {
    this.checkSelectedSeatsAndSelectedTickets.subscribe(() => this.informEverySector());
    this.chosenTickets = new Map<string, Ticket[]>();
    this.chosenTicketsForBackendSectors = new Map<Sector, Ticket[]>();
  }


  // eslint-disable-next-line @angular-eslint/use-lifecycle-interface
  ngDoCheck(): void {
    if (this.refreshed < 1) {
      this.sceneWidth = this.layout.eventHall.geometry.width * 0.5;
      this.sceneX = (this.layout.eventHall.geometry.width - this.sceneWidth) / 2;
      this.chosenTickets = new Map<string, Ticket[]>();
      this.layout.sectors.forEach((sector) => {
        if (!this.chosenTickets.has(sector.sectorId)) {
          this.chosenTickets.set(sector.sectorId, []);
        }
      });
    }
    this.refreshed++;
  }

  updateChosenTickets(sector: Sector, newTickets: Ticket[]) {
    const returnTickets: Ticket[] = [];


    this.chosenTicketsForBackendSectors.set(sector, newTickets);

    this.chosenTicketsForBackendSectors.forEach((value, key) => {

      if (key.sectorId === sector.sectorId) {
        const tempTick = this.chosenTicketsForBackendSectors.get(key);

        for (const tempAddTicket of tempTick) {
          returnTickets.push(tempAddTicket);
        }
      }
    });


    this.chosenTickets.set(sector.sectorId, returnTickets);

    const reallyReturnTickets: Ticket[] = [];
    this.chosenTicketsForBackendSectors.forEach((value, key) => {
      const tempTicket = this.chosenTicketsForBackendSectors.get(key);
      for (const tempAddTicket of tempTicket) {
        reallyReturnTickets.push(tempAddTicket);
      }
    });

    this.chosenTicketsEvent.emit(reallyReturnTickets);
    const returnMap = new Map<Sector, Ticket[]>();
    this.chosenTickets.forEach((tickets, sectorId) => {
      returnMap.set(this.layout.sectors.find((element) => element.sectorId === sectorId),
        returnMap.get(this.layout.sectors.find((element) => element.sectorId === sectorId)) === undefined ? tickets :
          returnMap.get(this.layout.sectors.find((element) => element.sectorId === sectorId)).concat(tickets));
    });

    this.sectorsWithTicketsEvent.emit(returnMap);
  }


  informEverySector(): void {
    this.performanceService.getLayoutOfPerformanceById(this.performanceId).subscribe({
      next: data => {
        for (const s of data.sectors) {
          this.informSectors.next(s);
        }
      }
    });
  }
}
