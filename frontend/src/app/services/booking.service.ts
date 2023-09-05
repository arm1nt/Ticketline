import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Order} from '../dtos/order';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private bookingBaseUri = this.globals.backendUri + '/orders';

  constructor(private http: HttpClient, private globals: Globals) {
  }

  /**
   * Get all orders stored in the system for the given user
   *
   * @param userId the id of the given user
   * @return observable list of found horses.
   */
  getAllBookings(userId: number): Observable<Order[]> {
    return this.http.get<Order[]>(this.bookingBaseUri + '/' + userId);
  }

  /**
   * Loads specific order from the backend
   *
   * @param id of order to load
   */
  getById(id: number): Observable<Order> {
    return this.http.get<Order>(this.bookingBaseUri + '/' + id);
  }

  /**
   * Create a new order in the system.
   *
   * @param order the data for the order that should be created
   * @return the id of the created Order
   */
  create(order: Order): Observable<Order> {

    return this.http.post<Order>(
        this.globals.backendUri + '/events/' + order.performanceId,
        order
    );
  }

  /**
   * Update am order in the system.
   *
   * @param order the data for the order that should be created
   * @return the id of the updated Order
   */
  update(order: Order): Observable<Order> {
    return this.http.put<Order>(
        this.bookingBaseUri + '/' + order.id,
        order
    );
  }

}
