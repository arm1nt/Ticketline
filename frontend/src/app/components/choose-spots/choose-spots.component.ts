import {Component, OnInit} from '@angular/core';
import {EventVenue} from '../../dtos/event-venue';
import {TicketStatus} from '../../enums/ticket-status';
import {Ticket} from '../../dtos/ticket';
import {Event} from '../../dtos/event';
import {Performance} from '../../dtos/performance';
import {Order} from '../../dtos/order';
import {OrderService} from '../../services/order.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PerformanceService} from '../../services/performance.service';
import {EventService} from '../../services/event.service';
import {Sector} from '../../dtos/sector';
import {EventhallService} from '../../services/eventhall.service';
import {LocationService} from '../../services/location.service';
import {EventHallOverview} from '../../dtos/event-hall';
import {Subject} from 'rxjs';
import {Layout} from '../../dtos/layout';

@Component({
  selector: 'app-choose-seats',
  templateUrl: './choose-spots.component.html',
  styleUrls: ['./choose-spots.component.scss']
})
export class ChooseSpotsComponent implements OnInit {

  updateSeatChart: Subject<void> = new Subject<void>();
  eventVenue: EventVenue;
  eventhall: EventHallOverview;
  performance: Performance;
  id = 0;
  refreshed = 0;
  order: Order = {
    id: null,
    performanceId: 0,
    tickets: []
  };
  sectorsWithTickets: Map<Sector, Ticket[]>;

  event: Event;

  layout: Layout;


  constructor(private eventService: EventService,
              private orderService: OrderService,
              private router: Router,
              private snackBar: MatSnackBar,
              private performanceService: PerformanceService,
              private eventhallService: EventhallService,
              private locationService: LocationService,
              private route: ActivatedRoute,
  ) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }


  ngOnInit(): void {
    this.id = +this.route.snapshot.paramMap.get('id');
    this.order.tickets = [];

  }

  // eslint-disable-next-line @angular-eslint/use-lifecycle-interface
  ngDoCheck(): void {
    if (this.refreshed < 1) {
      this.getPerformance();
      this.getLayout();
    }
    if (this.refreshed === 1) {
      this.order.performanceId = this.performance.id;
    }
    this.refreshed++;
  }

  getLayout() {
    this.performanceService.getLayoutOfPerformanceById(this.id).subscribe({
      next: layout => {
        this.layout = layout;
        this.getEventHall(layout.eventHall.id);
      },
      error: error => {
        this.snackBar.open('Unable to load the layout', 'Dismiss');
      }
    });
  }


  getPerformance(): void {
    this.performanceService.getById(this.id).subscribe({
      next: performance => {

        this.performance = performance;
        if (this.isInPast(performance.startTime)) {
          this.openSnackBar(`Performance is in past!`, 'Dismiss');
          this.router.navigate(['/event']);
        }
        this.getEvent(performance.eventId);
      }
    });
  }

  getEvent(id: number): void {
    this.eventService.getById(id).subscribe({
        next: event => {
          this.event = event;
        }
      }
    );
  }

  getEventHall(id: number): void {
    this.eventhallService.getById(id).subscribe({
        next: eventhall => {
          this.eventhall = eventhall;
        },
        error: error => {
          this.openSnackBar(`Eventhall couldn't load: ${error.error}`, 'Dismiss');
        }
      }
    );
  }

  newTicketsChosen(tickets: Ticket[]) {

    this.order.tickets = tickets;

  }

  newSectorsWithTicketsChosen(sectorsWithTickets: Map<Sector, Ticket[]>) {
    this.sectorsWithTickets = sectorsWithTickets;
  }

  hideButtons():
    boolean {
    return this.order.tickets.length === 0;
  }


  openSnackBar(message: string, action: string) {
    // eslint-disable-next-line max-len
    this.snackBar.open(message, action, {
      horizontalPosition: 'right',
      verticalPosition: 'top',
      duration: 3000,
      panelClass: ['snack-error']
    });
  }

  buyOrReserve(buy: boolean): void {
    const cusTicket: Ticket[] = [];

    this.order.tickets.forEach((t) => {
      if (buy) {
        const clone: Ticket = JSON.parse(JSON.stringify(t));
        clone.ticketStatus = TicketStatus.purchased;
        cusTicket.push(clone);
      } else {
        const clone: Ticket = JSON.parse(JSON.stringify(t));
        clone.ticketStatus = TicketStatus.reserved;
        cusTicket.push(clone);
      }
    });
    this.order.tickets = cusTicket;

    const observable = this.orderService.create(this.order);
    observable.subscribe(order => {
        this.order = order;
        this.router.navigate(['/success/' + order.id]);
      },
      error => {
        this.updateSeatChart.next();
        this.openSnackBar(`${error.error}`, 'Dismiss');
      });
  }

  isInPast(d: Date): boolean {
    return new Date(Date.now()) > new Date(d);
  }

  isAfter30MinutesBefore(d: Date): boolean {
    const now = Date.now();
    const deadline = Date.parse(new Date(d).toISOString());
    return Math.round((deadline - now) / 60000) < 30;
  }
}
