import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Performer, PerformerSearchResult} from '../dtos/performer';

@Injectable({
  providedIn: 'root'
})
export class PerformerService {

  private performerBaseUri: string = this.globals.backendUri + '/performer';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  /**
   * Loads all performers from the backend
   */
  getAll(): Observable<Performer[]> {
    return this.httpClient.get<Performer[]>(this.performerBaseUri);
  }

  search(firstname: string, lastname: string, artistname: string): Observable<PerformerSearchResult[]> {
    let params = new HttpParams();
    if (firstname !== undefined && firstname.trim() !== '') {
      params = params.set('firstname', firstname);
    }
    if (lastname !== undefined && lastname.trim() !== '') {
      params = params.set('lastname', lastname);
    }
    if (artistname !== undefined && artistname.trim() !== '') {
      params = params.set('artistname', artistname);
    }
    return this.httpClient.get<PerformerSearchResult[]>(this.performerBaseUri + '/search', {params});
  }
}
