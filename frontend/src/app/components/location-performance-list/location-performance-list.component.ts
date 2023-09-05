import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Location} from '../../dtos/location';
import {MatSnackBar} from '@angular/material/snack-bar';
import {LocationService} from '../../services/location.service';
import {Performance} from '../../dtos/performance';
import {PerformanceService} from '../../services/performance.service';

@Component({
  selector: 'app-location-performance-list',
  templateUrl: './location-performance-list.component.html',
  styleUrls: ['./location-performance-list.component.scss']
})
export class LocationPerformanceListComponent implements OnInit {

  location: Location;

  performances: Performance[];

  constructor(private route: ActivatedRoute,
              private performanceService: PerformanceService,
              private locationService: LocationService,
              private _snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.locationService.getById(Number(this.route.snapshot.paramMap.get('id'))).subscribe({
      next: data => {
        this.location = data;
        this.getPerformances(data.id);
      },
      error: error => {
        this.openSnackBar(`Error retrieving location`, 'Dismiss');
      }
    });
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action,
      {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snackbar-error']});
  }

  getPerformances(id: number) {
    this.performanceService.getPerformancesForLocation(id).subscribe({
      next: data => {
        this.performances = data;
      },
      error: error => {
        this.openSnackBar(`Error retrieving performances`, 'Dismiss');
      }
    });
  }

  toDateTimesStamp(date: Date): string {
    return new Date(date).toLocaleDateString() + ' at ' + new Date(date).toLocaleTimeString().substring(0, 5);
  }

  isInPast(d: Date): boolean {
    return new Date(Date.now()) > new Date(d);
  }
}
