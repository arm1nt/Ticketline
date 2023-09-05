import {Component, OnInit} from '@angular/core';
import {Event} from '../../dtos/event';
import {Router} from '@angular/router';
import {EventService} from '../../services/event.service';
import {NgForm} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {EventType} from '../../enums/event-type';
import {Performer} from '../../dtos/performer';
import {Performance} from '../../dtos/performance';
import {PerformerService} from '../../services/performer.service';
import {Observable} from 'rxjs';
import {EventCreation} from '../../enums/event-creation';
import {DatePipe} from '@angular/common';
import {LayoutService} from '../../services/layout.service';
import {LayoutOverview} from '../../dtos/layout';

@Component({
  selector: 'app-event-create',
  templateUrl: './event-create.component.html',
  styleUrls: ['./event-create.component.scss']
})
export class EventCreateComponent implements OnInit {
  pipe = new DatePipe('en-US');

  event = new Event();

  availableEventTypes: string[] = Object.values(EventType);
  availablePerformers: Observable<Performer[]>;
  performances: Observable<Performance[]>;
  displayedColumns: string[] = ['performanceName', 'startTime', 'endTime', 'layout'];
  eventCreationStage: EventCreation = EventCreation.creatingEvent;
  eventCreation = EventCreation;

  layoutMap: Map<number, LayoutOverview> = new Map<number, LayoutOverview>();

  constructor(
    private router: Router,
    private eventService: EventService,
    private layoutService: LayoutService,
    private _snackBar: MatSnackBar,
    private performerService: PerformerService,
  ) {
  }

  public get heading(): string {
    if (this.eventCreationStage === EventCreation.creatingEvent) {
      return 'Create an event';
    } else if (this.eventCreationStage === EventCreation.performanceOverview) {
      return 'Add Performances';
    }
  }

  public get uploadButtonText(): string {
    if (this.event.image != null) {
      return 'Upload completed';
    }
    return 'Upload Image';
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      this.event.name = this.event.name.trim();
      const observable = this.eventService.create(this.event);
      observable.subscribe({
        next: data => {
          this.openSnackBar(`Event "${this.event.name}" successfully created.`, 'Dismiss', true);
          this.event = data;
          this.eventCreationStage = this.eventCreation.performanceOverview;
        },
        error: error => {
          this.openSnackBar(`Event "${this.event.name}" couldn't be created.`, 'Dismiss', false);
        }
      });
    }
  }

  getLayout(performance: Performance): any {
    this.layoutService.getById(performance.layoutId).subscribe({
      next: layout => {
        this.layoutMap.set(layout.id, layout);
      },
      error: error => {
        this._snackBar.open('Error retrieving layout', 'Dismiss');
      }
    });
  }

  openSnackBar(message: string, action: string, success: boolean) {
    if(success) {
      this._snackBar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-success']});
    } else {
      this._snackBar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-error']});
    }
  }

  hasImageUploaded(): boolean {
    return this.event.image !== undefined;
  }

  onFileSelected() {
    const inputNode: any = document.querySelector('#file');
    if (typeof (FileReader) !== 'undefined') {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.event.image = (e.target.result);
      };
      reader.readAsDataURL(inputNode.files[0]);
    }
  }

  ngOnInit(): void {
    this.availablePerformers = this.performerService.getAll();
  }

  resetForm(): void {
    this.event.name = '';
    this.event.eventType = null;
    this.event.performers = null;
    this.event.image = null;
  }

  showOverviewPage() {
    this.eventCreationStage = EventCreation.performanceOverview;
    const observable = this.eventService.getById(this.event.id);
    observable.subscribe({
      next: data => {
        this.event = data;
        this.event.performances.forEach(p => {
          this.getLayout(p);
        });
      },
      error: error => {
        this.openSnackBar(`Error at overview page`, 'Dismiss', false);
      }
    });

  }

  toDateTimesStamp(date: Date):
    string {
    return new Date(date).toLocaleDateString() + ' ' + new Date(date).toLocaleTimeString();
  }

}
