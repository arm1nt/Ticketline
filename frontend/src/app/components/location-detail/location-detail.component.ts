import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {Location} from 'src/app/dtos/location';
import {LocationService} from 'src/app/services/location.service';

@Component({
  selector: 'app-location-detail',
  templateUrl: './location-detail.component.html',
  styleUrls: ['./location-detail.component.scss']
})
export class LocationDetailComponent implements OnInit, OnChanges {


  @Input() location: Location;
  @Input() enterEditMode: boolean;
  @Output() finishedEditing = new EventEmitter<boolean>();
  @Output() updatedLocations = new EventEmitter<true>();

  editMode = false;

  locationName = '';
  locationCountry = '';
  locationCity = '';
  locationStreet = '';
  locationZipCode = '';

  constructor(
    private snackbar: MatSnackBar,
    private locationService: LocationService,
    private router: Router
  ) { }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    if (this.location) {
      this.locationName = this.location.name;
      this.locationCountry = this.location.country;
      this.locationCity = this.location.city;
      this.locationStreet = this.location.street;
      this.locationZipCode = this.location.zipCode;
    }

    if (this.enterEditMode) {
      this.editMode = true;
      this.locationName = '';
      this.locationCountry = '';
      this.locationCity = '';
      this.locationStreet = '';
      this.locationZipCode = '';
    }
  }


  cancelCreation(): void {
    this.editMode = false;
    this.finishedEditing.emit(true);
  }


  createLocation(): void {
    const tempLocation: Location = {
      id: -1,
      name: this.locationName,
      country: this.locationCountry,
      city: this.locationCity,
      street: this.locationStreet,
      zipCode: this.locationZipCode
    };

    this.locationService.createLocation(tempLocation).subscribe({
      next: data => {
        //switch to selected mode give udpated location to parent component //call a net get all in parent
        this.snackbar.open('Location successfully added', 'Dismiss', {
          duration: 1500,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-success']
        });
        this.updatedLocations.emit(true);
        this.editMode = false;

      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });

  }

}
