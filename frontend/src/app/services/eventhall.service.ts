import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {CreateEventHall, EventHall, EventHallOverview} from '../dtos/event-hall';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventhallService {

  private eventhallsBaseUri: string = this.globals.backendUri + '/eventhalls';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }


  /**
   * Get all eventhalls stored in the persistent data store.
   *
   * @returns An array with all persisted eventhalls.
   */
  getAll(): Observable<EventHallOverview[]> {
    return this.http.get<EventHallOverview[]>(this.eventhallsBaseUri);
  }

  /**
   * Find a eventhall by name.
   *
   * @param name The name of the eventhall.
   * @returns Empty array if no such eventhall exists, otherwise array with 1 element.
   */
  getByName(name: string): Observable<EventHallOverview[]> {
    return this.http.get<EventHallOverview[]>(`${this.eventhallsBaseUri}?hallname=${name}`);
  }

  getHallsPaginated(page: number, size: number): Observable<EventHallOverview[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<EventHallOverview[]>(this.eventhallsBaseUri, {params});
  }

  /**
   * Get the eventhall specified by given id.
   *
   * @param id id of the eventhall that should be retrieved
   * @returns Eventhall with given id
   */
  getById(id: number): Observable<EventHall> {
    return this.http.get<EventHall>(`${this.eventhallsBaseUri}/${id}`);
  }

  /**
   * Create a new eventhall with a layout.
   *
   * @param eventHall Eventhall to be created
   * @returns Persisted eventhalls
   */
  createEventHall(eventHall: CreateEventHall): Observable<CreateEventHall> {
    return this.http.post<CreateEventHall>(this.eventhallsBaseUri, eventHall);
  }

  /**
   * Add a new layout to an already existing layout.
   *
   * @param eventHall Eventhall to which the layout should be added
   * @returns Persisted eventhall with added layout.
   */
  addLayoutToEventHall(eventHall: CreateEventHall): Observable<CreateEventHall> {
    return this.http.patch<CreateEventHall>(`${this.eventhallsBaseUri}/${eventHall.id}`, eventHall);
  }

  getEventhallsForLocation(locationId: number): Observable<EventHallOverview[]> {
    return this.http.get<EventHallOverview[]>(this.eventhallsBaseUri + `?locationId=` + locationId);
  }
}
