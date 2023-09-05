import {Component, OnInit} from '@angular/core';
import {debounceTime, Subject} from 'rxjs';
import {BreakpointObserver, BreakpointState} from '@angular/cdk/layout';
import {PerformerSearchResult} from '../../dtos/performer';
import {Location} from '../../dtos/location';
import {Event} from '../../dtos/event';
import {PerformerService} from '../../services/performer.service';
import {EventType} from '../../enums/event-type';
import {MatSnackBar} from '@angular/material/snack-bar';
import {EventService} from '../../services/event.service';
import {LocationService} from '../../services/location.service';
import {PerformanceSearch, PerformanceSearchResult} from '../../dtos/performance-search';
import {PerformanceService} from '../../services/performance.service';

class PerformerSearch {
  firstName: string;
  lastName: string;
  artistName: string;
}

class LocationSearch {
  name: string;
  country: string;
  city: string;
  street: string;
  zipCode: number;
}

class EventSearch {
  name: string;
  eventType: EventType;
  duration: number;
  tolerance: number;
}

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  maxPrice = 0;
  concertEvent = EventType.concert;
  festivalEvent = EventType.festival;
  ballEvent = EventType.ball;
  operaEvent = EventType.opera;

  crop = false;

  searchLocation = true;
  searchEvent = false;
  searchArtist = false;
  searchPerformance = false;

  //bind location search parameters
  locationSearch: LocationSearch;

  eventSearch: EventSearch;

  //bind artist search parameters
  performerSearch: PerformerSearch;


  //bind time search parameters
  performanceSearch: PerformanceSearch;

  performanceResults: PerformanceSearchResult[];


  //subjects to debounce searches
  locationSearchSubject = new Subject<LocationSearch>();
  eventSearchSubject = new Subject<EventSearch>();
  artistSearchSubject = new Subject<PerformerSearch>();
  performanceSearchSubject = new Subject<PerformanceSearch>();


  performerSearchResults: PerformerSearchResult[];
  locationSearchResults: Location[];

  eventSearchResults: Event[];

  concertType = EventType.concert;
  festivalType = EventType.festival;
  ballType = EventType.ball;
  operaType = EventType.ball;

  constructor(
    private responsive: BreakpointObserver,
    private performerService: PerformerService,
    private eventService: EventService,
    private locationService: LocationService,
    private performanceService: PerformanceService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.locationSearch = new LocationSearch();
    this.locationSearchSubject.pipe(debounceTime(300)).subscribe(_search => this.locationPerformSearch(this.locationSearch));

    this.eventSearch = new EventSearch();
    this.eventSearchSubject.pipe(debounceTime(300)).subscribe(_search => this.eventPerformSearch(this.eventSearch));

    this.performerSearch = new PerformerSearch();
    this.artistSearchSubject.pipe(debounceTime(300)).subscribe(_search => this.artistPerformSearch(this.performerSearch));

    this.performanceService.getMaxPrice().subscribe({
      next: (price) => this.maxPrice = price,
      error: () => this.maxPrice = 0
    });

    this.performanceSearch = new PerformanceSearch();
    this.performanceSearchSubject.pipe(debounceTime(300)).subscribe(_search => this.performancePerformSearch(this.performanceSearch));


    this.responsive
      .observe(['(min-width: 800px)'])
      .subscribe((state: BreakpointState) => {
        this.crop = !state.matches;
      });
  }

  formatLabel(value: number): string {
    return `${value}€`;
  }

  locationPerformSearch(search: LocationSearch) {
    if (!this.locationSearchParamsAreEmpty(search)) {
      this.locationService.search(search.name,
        search.street,
        search.city,
        search.country,
        (search.zipCode ? search.zipCode.toString() : undefined)).subscribe({
        next: data => {
          this.locationSearchResults = data;
        }, error: error => {
          this.snackBar.open('An error occurred while searching for locations: ' + error.error, null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
        }
      });
    } else {
      this.locationSearchResults = [];
    }
  }

  eventPerformSearch(search: EventSearch) {
    if (!this.eventSearchParamsAreEmpty(search)) {
      this.eventService.search(search.name,
        search.eventType,
        search.duration,
        search.tolerance).subscribe({
        next: data => {
          this.eventSearchResults = data;
        }, error: error => {
          this.snackBar.open('An error occurred while searching for events: ' + error.error, null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snack-error']
          });
        }
      });
    } else {
      this.eventSearchResults = [];
    }
  }

  artistPerformSearch(search: PerformerSearch) {
    if (search.firstName !== null || search.lastName !== null || search.artistName !== null) {
      this.performerService.search(search.firstName, search.lastName, search.artistName).subscribe({
        next: data => {
          this.performerSearchResults = data;
        }, error: error => {
          console.error(error);
          this.snackBar.open('An error occurred while searching for performances: ' + error.error, null, {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['snackbar-error']
          });
        }
      });
    }
  }

  performancePerformSearch(search: PerformanceSearch) {
    this.performanceService.searchForPerformance(search).subscribe({
      next: data => {
        this.performanceResults = data;
      },
      error: error => {
        let newError = 'Unknown error';
        if (error.error !== null) {
          newError = error.error;
        }
        this.snackBar.open('An error occurred while searching for performances: ' + newError, null, {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['snack-error']
        });
      }
    });
  }

  searchForLocation():
    void {
    this.searchLocation = true;
    this.searchEvent = false;
    this.searchArtist = false;
    this.searchPerformance = false;
    this.clearSearches();
  }

  searchForEvent():
    void {
    this.searchEvent = true;
    this.searchLocation = false;
    this.searchArtist = false;
    this.searchPerformance = false;
    this.clearSearches();
  }

  searchForArtist():
    void {
    this.searchArtist = true;
    this.searchLocation = false;
    this.searchEvent = false;
    this.searchPerformance = false;
    this.clearSearches();
  }

  searchForPerformance():
    void {
    this.searchPerformance = true;
    this.searchLocation = false;
    this.searchEvent = false;
    this.searchArtist = false;
    this.clearSearches();
  }

  clearLocationSearch():
    void {
    this.locationSearch = new LocationSearch();
  }

  clearEventSearch():
    void {
    this.eventSearch = new EventSearch();
  }

  clearArtistSearch():
    void {
    this.performerSearch = new PerformerSearch();
  }

  clearperformanceSearch():
    void {
    this.performanceSearch = new PerformanceSearch();
  }

  clearSearches():
    void {
    this.clearLocationSearch();
    this.clearEventSearch();
    this.clearArtistSearch();
    this.clearperformanceSearch();
  }

  clearInputAndSearch():
    void {
    this.clearSearches();
  }

  searchLocationServer(search: LocationSearch):
    void {
    this.locationSearchSubject.next(search);
  }

  searchEventServer(search: EventSearch):
    void {
    this.eventSearchSubject.next(search);
  }


  searchArtistServer(search:
                       PerformerSearch
  ):
    void {
    this.artistSearchSubject.next(search);
  }

  /**
   * add jsdoc
   */
  searchPerformanceServer(search: PerformanceSearch): void {
    this.performanceSearchSubject.next(search);
  }

  toDateTimesStamp(date: Date):
    string {
    return new Date(date).toLocaleDateString() + ' ' + new Date(date).toLocaleTimeString();
  }


  isInPast(d: Date): boolean {
    return new Date(Date.now()) > new Date(d);
  }

  private locationSearchParamsAreEmpty(search: LocationSearch): boolean {
    return (!search.name || search.name === '') &&
      (!search.street || search.street === '') &&
      (!search.city || search.city === '') &&
      (!search.country || search.country === '') &&
      (!search.zipCode);
  }

  private eventSearchParamsAreEmpty(search: EventSearch): boolean {
    return (!search.name || search.name === '') &&
      !search.eventType &&
      !search.duration &&
      !search.tolerance;
  }
}
