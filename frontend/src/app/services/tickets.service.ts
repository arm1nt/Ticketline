import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';
import { TicketPdf } from '../dtos/ticketPdf';

@Injectable({
  providedIn: 'root'
})
export class TicketsService {

  private tickerBaseUri: string = this.globals.backendUri + '/tickets';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) { }

  /**
   * Returns the pdf (as base64 decoded string) of the ticket specified by the given id.
   *
   */
  getTicket(id: number): Observable<TicketPdf> {
    return this.http.get<TicketPdf>(this.tickerBaseUri + `/pdf/${id}`);
  }

}
