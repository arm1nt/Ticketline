import {Component, OnInit} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {Location} from 'src/app/dtos/location';
import {LocationService} from 'src/app/services/location.service';

@Component({
  selector: 'app-create-location',
  templateUrl: './create-location.component.html',
  styleUrls: ['./create-location.component.scss']
})
export class CreateLocationComponent implements OnInit {


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
        this.snackbar.open('Location successfully added', 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-success']
        });
        this.router.navigate(['/']);
      },
      error: error => {
        this.snackbar.open(`${error.error}`, 'Dismiss', {
          duration: 2000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });

  }



}
