import { Injectable } from '@angular/core';
import {Artist} from '../dtos/artist';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Message} from '../dtos/message';


@Injectable({
  providedIn: 'root'
})
export class ArtistService {

  private artistBaseUri: string = this.globals.backendUri + '/artist';

  constructor(private httpClient: HttpClient, private globals: Globals) {

  }

  /**
   * Loads all artists from the backend
   */
  getAll(): Observable<Artist[]> {
    return this.httpClient.get<Artist[]>(this.artistBaseUri);
  }

  addArtist(artist: Artist): Observable<Artist> {
    return this.httpClient.post<Artist>(this.artistBaseUri, artist);
  }
}
