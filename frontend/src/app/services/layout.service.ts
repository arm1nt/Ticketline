import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {CreateLayout, LayoutOverview} from '../dtos/layout';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CreateEventHall} from '../dtos/event-hall';

@Injectable({
  providedIn: 'root'
})
export class LayoutService {

  private layoutBaseUri: string = this.globals.backendUri + '/layouts';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }


  /**
   * Create a new Layout.
   *
   * @param layout newly created layout
   * @returns persisted layout
   */
  createLayout(layout: CreateEventHall): Observable<CreateLayout> {
    return this.http.post<CreateLayout>(this.layoutBaseUri, layout);
  }

  getAll(): Observable<LayoutOverview[]> {
    return this.http.get<LayoutOverview[]>(this.layoutBaseUri);
  }

  getById(id: number): Observable<LayoutOverview> {
    return this.http.get<LayoutOverview>(this.layoutBaseUri + '/' + id);
  }

  getLayoutsForEventHall(evenHallId: number): Observable<LayoutOverview[]> {
    return this.http.get<LayoutOverview[]>(this.layoutBaseUri + `?evenHallId=` + evenHallId);
  }

  getAllByName(name: string): Observable<LayoutOverview[]> {
    return this.http.get<LayoutOverview[]>(this.layoutBaseUri + `?name=` + name);
  }
}
