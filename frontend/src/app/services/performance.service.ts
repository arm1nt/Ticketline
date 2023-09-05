import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {map, Observable} from 'rxjs';
import {Performance} from '../dtos/performance';
import {Layout} from '../dtos/layout';
import {Seating} from '../dtos/seating';
import {RectangleGeometry} from '../dtos/rectangle-geometry';
import {Row} from '../dtos/row';
import {Seat} from '../dtos/seat';
import {Ticket} from '../dtos/ticket';
import {TicketStatus} from '../enums/ticket-status';
import {SeatGeometry} from '../dtos/seat-geometry';
import {Geometry} from '../dtos/geometry';
import {Standing} from '../dtos/standing';
import {Stand} from '../dtos/stand';
import {PerformanceSearch, PerformanceSearchResult} from '../dtos/performance-search';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {

  private performanceBaseUri: string = this.globals.backendUri + '/performance';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  /**
   * Loads all performances from the backend
   */
  getAll(): Observable<Performance[]> {
    return this.httpClient.get<Performance[]>(this.performanceBaseUri);
  }

  getPerformancesForLocation(locationId: number): Observable<Performance[]> {
    let params = new HttpParams();
    if (locationId !== undefined) {
      params = params.set('locationId', locationId);
    }
    return this.httpClient.get<Performance[]>(this.performanceBaseUri, {params});
  }

  create(performance: Performance): Observable<Performance> {
    return this.httpClient.post<Performance>(this.performanceBaseUri, performance);
  }

  getById(id: number): Observable<Performance> {
    return this.httpClient.get<Performance>(this.performanceBaseUri + '/' + id);
  }

  getLayoutOfPerformanceById(id: number): Observable<Layout> {
    return this.httpClient.get<Layout>(this.performanceBaseUri + '/' + id + '/layout').pipe(
      map(data => this.mapToLayout(data)));
  }

  getMaxPrice(): Observable<number> {
    return this.httpClient.get<number>(this.performanceBaseUri + '/maxPrice');
  }

  searchForPerformance(search: PerformanceSearch): Observable<PerformanceSearchResult[]> {
    const newSearch = {...search, time: search.time !== undefined ? this.convertDateToString(search.time): undefined};
    Object.keys(newSearch)
      .forEach(key => newSearch[key] === undefined || newSearch[key] === '' || newSearch[key] === null ? delete newSearch[key] : {});
    const params = new HttpParams({fromObject: newSearch});
    return this.httpClient.get<PerformanceSearchResult[]>(this.performanceBaseUri + '/search', {params});
  }

  /**
   * Maps the data that is received from the server to Layout and Seatings or Standings objects in order to
   * differentiate those types in the frontend.
   *
   * @param obj Data that is received from the backend.
   * @private Mapped Layout.
   */
  private mapToLayout(obj): Layout {
    return new Layout(obj.id, obj.eventHall, obj.sectors.map(sector => sector.rows !== undefined ?
      new Seating(sector.id, sector.sectorId, sector.price, sector.color,
        new RectangleGeometry(sector.geometry.x, sector.geometry.y, sector.geometry.rotation, sector.geometry.width,
          sector.geometry.height),
        sector.rows.map(row =>
          new Row(row.id, row.rowNumber, row.seats.map(seat =>
              new Seat(seat.id, seat.seatId, new Ticket(seat.ticket.id, seat.ticket.ticketId,
                  TicketStatus[seat.ticket.ticketStatus.toLowerCase() as keyof typeof TicketStatus]),
                new SeatGeometry(seat.geometry.x, seat.geometry.y, seat.geometry.rotation, seat.geometry.width,
                  seat.geometry.height, seat.geometry.legSpaceDepth), false)),
            new Geometry(row.geometry.x, row.geometry.y, row.geometry.rotation)))) :
      new Standing(sector.id, sector.sectorId, sector.price, sector.color,
        new RectangleGeometry(sector.geometry.x, sector.geometry.y, sector.geometry.rotation, sector.geometry.width,
          sector.geometry.height),
        sector.capacity,
        sector.stands.map(stand =>
          new Stand(stand.id, new Ticket(stand.ticket.id, stand.ticket.ticketId,
            TicketStatus[stand.ticket.ticketStatus.toLowerCase() as keyof typeof TicketStatus]), false)))));
  }

  private convertDateToString(date: Date | string): string {
    if (typeof date == 'string') {
      return date;
    } else {
      return date.toISOString();
    }
  }
}
