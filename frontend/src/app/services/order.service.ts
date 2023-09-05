import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Invoice, Order} from '../dtos/order';
import {Globals} from '../global/globals';
import {OrderOverview} from '../dtos/orderoverview';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private customersBaseUri: string = this.globals.backendUri + '/orders';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Returns the invoice as string that can be turned into a pdf.
   *
   */
  getInvoice(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(this.customersBaseUri + `/invoice/${id}`);
  }

  /**
   * Returns the cancellation as string that can be turned into a pdf.
   *
   */
  getCancellation(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(this.customersBaseUri + `/cancellation/${id}`);
  }

  /**
   * Get all orders belonging to the user.
   *
   */
  getAllOrders(): Observable<OrderOverview[]> {
    return this.http.get<OrderOverview[]>(this.customersBaseUri);
  }

  /**
   * Get the order by id if this order belongs to the user requesting it.
   *
   * @param orderId id of the order
   */
  getById(orderId: number): Observable<Order> {
    return this.http.get<Order>(this.customersBaseUri + `/${orderId}`);
  }


  /**
   * Creates a payment or a reservation
   *
   * @param order order with the tickets, performance id, etc.
   */
  create(order: Order): Observable<Order> {
    return this.http.post<Order>(this.customersBaseUri, order);
  }

  /**
   * Creates a payment or a reservation
   *
   * @param order order with the tickets, performance id, etc.
   */
  createOld(order: Order): Observable<Order> {
    return this.http.post<Order>(this.customersBaseUri, order);
  }

  /**
   * Updates an order by either cancelling it (bzw. some of the tickets) or by upgrading the reservation to a purchase
   *
   * @param order order that is concerned by this operation
   */
  update(order: Order): Observable<Order> {
    return this.http.put<Order>(this.customersBaseUri + `/${order.id}`, order);
  }


}
