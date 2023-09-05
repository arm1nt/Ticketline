import {Component, OnInit} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TemporaryEventHallInformation} from 'src/app/dtos/event-hall';
import {Location} from 'src/app/dtos/location';
import {EventhallService} from 'src/app/services/eventhall.service';
import {LocationService} from 'src/app/services/location.service';

@Component({
  selector: 'app-create-eventhall',
  templateUrl: './create-eventhall.component.html',
  styleUrls: ['./create-eventhall.component.scss']
})
export class CreateEventhallComponent implements OnInit {

  createEventhallFlag = true;
  createLayoutFlag = false;
  eventHallInformation: TemporaryEventHallInformation = undefined;

  eventHallName = '';

  locations: Location[] = [];
  selectedLocation: Location = undefined;

  createLocationFlag = false;

  constructor(
    private locationService: LocationService,
    private eventHallService: EventhallService,
    private snackbar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.locationService.getAll().subscribe({
      next: data => {
        this.locations = data;
        if( this.locations.length > 0) {
          this.selectedLocation = this.locations[0];
        }
      },
      error: error => {
        this.snackbar.open(`${error.errors}`, 'Dismiss', {
          duration: 1500,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  finishedEditingParent(): void {
    this.createLocationFlag = false;
  }

  childAddedLocation(): void {

    const storeTemp: Location = this.selectedLocation;

    this.createLocationFlag = false;

    this.locationService.getAll().subscribe({
      next: data => {
        this.locations = data;
        this.selectedLocation = undefined;
      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 1500,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  createNewLocation(): void {
    this.createLocationFlag = true;
  }

  goToCreateLayout(): void {

    this.eventHallService.getByName(this.eventHallName).subscribe({
      next: data => {
        if(data.length > 0) {
          this.snackbar.open('Error: A eventhall with this name already exists', 'Dismiss', {
            duration: 1300,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
          return;
        }
        this.createLayoutFlag = true;

        this.eventHallInformation = {
          name: this.eventHallName,
          location: this.selectedLocation
        };
      },
      error: error => {
        this.snackbar.open(`${error.error.error}`, 'Dismiss', {
          duration: 1300,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
    return;

    this.createLayoutFlag = true;

    this.eventHallInformation = {
      name: this.eventHallName,
      location: this.selectedLocation
    };
  }
}
