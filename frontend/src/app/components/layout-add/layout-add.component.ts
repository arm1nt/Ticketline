import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EventHallOverview } from 'src/app/dtos/event-hall';
import { EventhallService } from 'src/app/services/eventhall.service';
import { TemporaryEventHallInformation } from 'src/app/dtos/event-hall';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-layout-add',
  templateUrl: './layout-add.component.html',
  styleUrls: ['./layout-add.component.scss']
})
export class LayoutAddComponent implements OnInit {

  editLayoutFlag = false;

  eventHallInformation: TemporaryEventHallInformation = undefined;
  eventHallId: number = undefined;
  selectedEvent: EventHallOverview = undefined;

  existingEventHalls: EventHallOverview[] =  [];

  pageSize = 5;
  columnsToShow = ['hallname', 'chooseHall'];
  listEventhalls: MatTableDataSource<EventHallOverview>;
  numberOfEventhalls = 0;
  selectedID = -1;
  selectedHall: string = undefined;

  constructor(
    private eventHallService: EventhallService,
    private snackbar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.eventHallService.getAll().subscribe({
      next: data => {
        this.numberOfEventhalls = data.length;
      },
      error: error => {
        this.snackbar.open('Error fetching eventhalls', 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
    this.getEventhalls(0);
  }

  nextPage(event: PageEvent) {
    this.getEventhalls(event.pageIndex);
  }

  selectThisHall(hall: string, id: number): void {
    this.selectedHall = hall;
    this.selectedID = id;
  }


  editLayout(): void {

    this.eventHallInformation = {
      name: this.selectedHall,
      location: {
        id: -1,
        name: 'location',
        country: 'country',
        city: 'city',
        street: 'street',
        zipCode: 'zipCode'
      }
    };
    this.eventHallId = this.selectedID;
    this.editLayoutFlag = true;
  }

  private getEventhalls(page: number) {
    this.eventHallService.getHallsPaginated(page, this.pageSize).subscribe({
      next: data => {
        this.listEventhalls = new MatTableDataSource(data);

      },
      error: error => {
        this.snackbar.open('An error occured fetching eventhalls: ' + error.error, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }
}
