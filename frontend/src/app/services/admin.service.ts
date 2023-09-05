import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User, UserCreation, UserShort} from '../dtos/user';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private adminBaseUri: string = this.globals.backendUri + '/admin';

  constructor(
    private globals: Globals,
    private http: HttpClient
  ) { }

  /**
   * Create a new user
   *
   * @param userCreation Dto with user details.
   */
  createUser(userCreation: UserCreation): Observable<User> {
    return this.http.post<User>(this.adminBaseUri , userCreation);
  }

  /**
   * Get all locked user accounts.
   */
  getLockedUsers(): Observable<UserShort[]> {
    return this.http.get<UserShort[]>(this.adminBaseUri + '/customers/locked');
  }

  /**
   * Unlock a user account.
   *
   * @param username The username of the user to unlock.
   */
  unlockUser(username: string): Observable<void> {
    const params = new HttpParams().set('username', username);
    return this.http.put<void>(this.adminBaseUri + '/customers', null,  {params});
  }

  /**
   * Get all users, which are not admins, but paginated.
   *
   * @param page The page number.
   * @param size The number of elements per page.
   */
  findAllNonAdminsOrderedByUsername(page: number, size: number): Observable<UserShort[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<UserShort[]>(this.adminBaseUri + '/customers', {params});
  }

  /**
   * Lock a user account. The user should not be an admin.
   *
   * @param username The username of the user to lock.
   */
  lockUser(username: string): Observable<void> {
    const params = new HttpParams().set('username', username);
    return this.http.put<void>(this.adminBaseUri + '/lock', null,  {params});
  }

  resetPassword(username: string): Observable<void> {
    const params = new HttpParams().set('username', username);
    return this.http.post<void>(this.adminBaseUri + '/reset', null,  {params});
  }
}
