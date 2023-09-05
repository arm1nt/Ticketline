import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Location} from '../dtos/location';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/locations';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals
  ) {
  }


  /**
   * Get all the locations stored in the database.
   *
   */
  getAll(): Observable<Location[]> {
    return this.httpClient.get<Location[]>(this.locationBaseUri);
  }

  /**
   * Get location by id
   *
   * @param id id of the location to be retrieved
   * @returns retrieved location
   */
  getById(id: number): Observable<Location> {
    return this.httpClient.get<Location>(this.locationBaseUri + `/${id}`);
  }

  /**
   * Create location in backend
   *
   * @param location location to be stored in the backend
   * @returns persisted location
   */
  createLocation(location: Location): Observable<Location> {
    return this.httpClient.post<Location>(this.locationBaseUri, location);
  }

  search(name: string,
         street: string,
         city: string,
         country: string,
         zipCode: string): Observable<Location[]> {
    let params = new HttpParams();
    if (name !== undefined && name.trim() !== '') {
      params = params.set('name', name);
    }
    if (street !== undefined && street.trim() !== '') {
      params = params.set('street', street);
    }
    if (city !== undefined && city.trim() !== '') {
      params = params.set('city', city);
    }
    if (country !== undefined && country.trim() !== '') {
      params = params.set('country', country);
    }
    if (zipCode !== undefined) {
      params = params.set('zipCode', zipCode);
    }
    return this.httpClient.get<Location[]>(this.locationBaseUri + '/search', {params});
  }
}
