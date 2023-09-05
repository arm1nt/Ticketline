import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Band} from '../dtos/band';
import {Artist} from '../dtos/artist';

@Injectable({
  providedIn: 'root'
})
export class BandService {

  private bandBaseUri: string = this.globals.backendUri + '/band';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  /**
   * Loads all bands from the backend
   */
  getAll(): Observable<Band[]> {
    return this.httpClient.get<Band[]>(this.bandBaseUri);
  }

  addBand(band: Band): Observable<Band> {
    return this.httpClient.post<Band>(this.bandBaseUri, band);
  }
}
