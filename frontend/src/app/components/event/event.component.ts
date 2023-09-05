import {Component, OnInit} from '@angular/core';
import {Event} from '../../dtos/event';
import {AuthService} from '../../services/auth.service';
import {EventService} from '../../services/event.service';
import {ActivatedRoute} from '@angular/router';
import {Performer} from '../../dtos/performer';
import {PageEvent} from '@angular/material/paginator';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-event',
  templateUrl: './event.component.html',
  styleUrls: ['./event.component.scss']
})
export class EventComponent implements OnInit {

  numberOfEvents = 0;
  pageSize = 6;
  events: Event[];

  constructor(
    private authService: AuthService,
    private eventService: EventService,
    private route: ActivatedRoute,
    private snackbar: MatSnackBar
  ) {
  }


  ngOnInit(): void {
    this.eventService.getAll().subscribe({
      next: (events: Event[]) => {
        this.events = events.slice(0, this.pageSize);
        this.numberOfEvents = events.length;
      },
      error: error => {}
    });
  }

  nextPage(event: PageEvent) {
    this.eventService.getAllPaged(event.pageIndex, this.pageSize).subscribe({
      next: data => {
        this.events = data;
      },
      error: err => {
        this.snackbar.open('Error retrieving the events', 'Dismiss');
      }
    });
  }


  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  cheapestAvailableTicketPrice(event: Event): number {
    return 0;
  }

  ticketsAvailable(event: Event): boolean {
    return true;
  }


  dateAsLocalDateString(date: Date): string {
    return date.toLocaleDateString();
  }

  toStringList(performerList: Performer[]): string {
    const performerNames: string[] = [];
    for (const item of performerList) {
      performerNames.push(item.performerName);
    }
    return performerNames.join(', ');
  }
}
