import {Component, OnInit} from '@angular/core';
import {Event} from '../../dtos/event';
import {ActivatedRoute} from '@angular/router';
import {EventService} from '../../services/event.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-event-detail',
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss']
})
export class EventDetailComponent implements OnInit {
  event = new Event();
  displayedColumns: string[] = ['performanceName', 'startTime', 'endTime', 'actions'];

  constructor(  private route: ActivatedRoute,
                private eventService: EventService,
                private _snackBar: MatSnackBar,) {

  }

  ngOnInit(): void {
    const observable = this.eventService.getById(Number(this.route.snapshot.paramMap.get('id')));
    observable.subscribe({
      next: data => {
        this.event = data;
      },
      error: error => {
        this.openSnackBar(`Error retrieving event`, 'Dismiss');
      }
    });
  }

  isInPast(d: Date): boolean {
    return new Date(Date.now()) > new Date(d);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action,
      {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-error']});
  }


  toDateTimesStamp(date: Date):
    string {
    return new Date(date).toLocaleDateString() + ' ' + new Date(date).toLocaleTimeString();
  }
}

