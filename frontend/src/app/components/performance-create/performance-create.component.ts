import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {NgForm} from '@angular/forms';
import {Event} from '../../dtos/event';
import {Performance} from '../../dtos/performance';
import {Location} from '../../dtos/location';
import {Router} from '@angular/router';
import {EventService} from '../../services/event.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PerformanceService} from '../../services/performance.service';
import {Layout, LayoutOverview} from '../../dtos/layout';
import {LocationService} from '../../services/location.service';
import {EventHallOverview} from '../../dtos/event-hall';
import {LayoutService} from '../../services/layout.service';
import {EventhallService} from '../../services/eventhall.service';

@Component({
  selector: 'app-performance-create',
  templateUrl: './performance-create.component.html',
  styleUrls: ['./performance-create.component.scss']
})
export class PerformanceCreateComponent implements OnInit, OnChanges {

  @Input() event: Event;
  @Input() toEvent: boolean;
  // eslint-disable-next-line @angular-eslint/no-output-native
  @Output() close = new EventEmitter<boolean>();

  eventname: string;
  endtime: string;
  layout: Layout;
  performance: Performance = {
    id: null,
    performanceName: null,
    startTime: null,
    endTime: null,
    layoutId: null,
    eventId: null,
    locationId: null,
    eventhallId: null
  };
  availableEvents: Event[];
  availableLayouts: LayoutOverview[];
  availableLocations: Location[];
  availableEventHalls: EventHallOverview[];

  constructor(
    private router: Router,
    private eventService: EventService,
    private layoutService: LayoutService,
    private performanceService: PerformanceService,
    private locationService: LocationService,
    private eventhallService: EventhallService,
    private snackBar: MatSnackBar,
  ) {
  }

  public get heading(): string {
    return 'Add performance';
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      console.log('onSubmit', this.performance);
      const observable = this.performanceService.create(this.performance);
      observable.subscribe({
        next: _data => {
          this.openSnackBar(`Performance successfully created.`, 'Dismiss', true);
          this.close.emit(true);
          if (!this.toEvent) {
            this.router.navigate(['/event']);
          }
        },
        error: error => {
          console.log(error);
          this.openSnackBar(`Performance couldn't be created: ${error.error}`, 'Dismiss', false);
        }
      });
    }
  }

  openSnackBar(message: string, action: string, success: boolean) {
    if(success) {
      this.snackBar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-success']});
    } else {
      this.snackBar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-error']});
    }
  }

  ngOnInit(): void {
    this.endtime = '';
    if (this.event !== undefined) {
      this.performance.eventId = this.event.id;
      this.eventname = this.event.name;
    }
    if (this.layout !== undefined) {
      this.performance.layoutId = this.layout.id;
    }
    this.eventService.getAll().subscribe({
      next: (events: Event[]) => {
        this.availableEvents = events;
      }, error: error => {
        this.snackBar.open('An error occurred while loading events: ' + error.error, null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
    this.locationService.getAll().subscribe({
      next: (locations: Location[]) => {
        this.availableLocations = locations;
      }, error: error => {
        this.snackBar.open('An error occurred while loading locations: ' + error.error, null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  ngOnChanges():
    void {
    console.log(this.event);
  }

  resetForm():
    void {
    this.performance.eventId = null;
    this.performance.performanceName = null;
    this.performance.startTime = null;
    this.performance.endTime = null;
    this.performance.layoutId = null;
  }

  loadEventHalls(locationId: number) {
    if (locationId !== null) {
      this.eventhallService.getEventhallsForLocation(locationId).subscribe({
        next: (eventhalls: EventHallOverview[]) => {
          this.availableEventHalls = eventhalls;
          this.performance.layoutId = null;
          this.performance.eventhallId = null;
          console.log(this.availableEventHalls);
        }, error: error => {
          this.snackBar.open('An error occurred while loading eventhalls: ' + error.error, null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
        }
      });
    }
  }

  loadLayouts(eventhallId: number) {
    if (eventhallId !== null) {
      this.layoutService.getLayoutsForEventHall(eventhallId).subscribe({
        next: (layouts: LayoutOverview[]) => {
          this.availableLayouts = layouts;
          this.performance.layoutId = null;
        }, error: error => {
          this.snackBar.open('An error occurred while loading layouts: ' + error.error, null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
        }
      });
    }
  }
}
