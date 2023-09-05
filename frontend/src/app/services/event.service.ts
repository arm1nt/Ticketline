import {Injectable} from '@angular/core';
import {Event} from '../dtos/event';
import {map, Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Layout} from '../dtos/layout';
import {Seating} from '../dtos/seating';
import {Geometry} from '../dtos/geometry';
import {Ticket} from '../dtos/ticket';
import {Stand} from '../dtos/stand';
import {Seat} from '../dtos/seat';
import {RectangleGeometry} from '../dtos/rectangle-geometry';
import {Row} from '../dtos/row';
import {TicketStatus} from '../enums/ticket-status';
import {Standing} from '../dtos/standing';
import {SeatGeometry} from '../dtos/seat-geometry';
import {EventType} from '../enums/event-type';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventBaseUri: string = this.globals.backendUri + '/events';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getAllPaged(page: number, size: number): Observable<Event[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.httpClient.get<Event[]>(this.eventBaseUri, {params});
  }

  /**
   * Loads all events from the backend
   */
  getAll(): Observable<Event[]> {
    return this.httpClient.get<Event[]>(this.eventBaseUri);
  }

  create(event: Event): Observable<Event> {
    return this.httpClient.post<Event>(this.eventBaseUri, event);
  }

  getById(id: number): Observable<Event> {
    return this.httpClient.get<Event>(this.eventBaseUri + '/' + id);
  }

  /**
   * Loads specific layout from the backend
   *
   * @param id of message to load
   */
  getLayout(id: number): Observable<Layout> {
    return this.httpClient.get<Layout>(this.eventBaseUri + '/layout/' + id).pipe(
      map(data => this.mapToLayout(data)));
  }

  getAllLayouts(): Observable<Layout[]> {
    return this.httpClient.get<Layout[]>(this.eventBaseUri + '/layout');
  }

  getTop10Events(eventType: string): Observable<Event[]> {
    return this.httpClient.get<(Event[])>(this.eventBaseUri + '/top10?eventType=' + eventType);
  }
  search(name: string, eventType: EventType, duration: number, tolerance: number): Observable<Event[]> {
    let params = new HttpParams();
    if (name !== undefined && name.trim() !== '') {
      params = params.set('name', name);
    }
    if (eventType !== undefined) {
      params = params.set('eventType', eventType);
    }
    if (duration !== undefined && duration !== null) {
      params = params.set('duration', duration);
    }
    if (tolerance !== undefined && tolerance !== null) {
      params = params.set('tolerance', tolerance);
    }
    return this.httpClient.get<Event[]>(this.eventBaseUri, {params});
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
              new Seat(seat.id, seat.seatId,
                new Ticket(seat.ticket.id, seat.ticket.ticketId,
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


}
