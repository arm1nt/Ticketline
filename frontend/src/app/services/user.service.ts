import {HttpClient, HttpParams} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {ResetPassword, UpdatePassword, UpdateUser, User, UserRegistration} from '../dtos/user';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private customersBaseUri: string = this.globals.backendUri + '/customers';

  constructor(
    private http: HttpClient,
    private globals: Globals,
  ) { }


  /**
   * Get details of the current user.
   *
   */
  getDetails(): Observable<User> {
    return this.http.get<User>(this.customersBaseUri);
  }

  /**
   * Update the first name of the user in the backend.
   *
   * @param editFirstname dto that contains the edited firstname
   * @returns edited user
   */
  updateFirstname(editFirstname: UpdateUser): Observable<User> {
    return this.http.patch<User>(this.customersBaseUri, editFirstname);
  }

  /**
   * Update the last name of the user in the backend.
   *
   * @param editLastname dto that contains the edited lastname
   * @returns edited user
   */
  updateLastname(editLastname: UpdateUser): Observable<User> {
    return this.http.patch<User>(this.customersBaseUri, editLastname);
  }

  /**
   * Update the email of the user in the backend.
   *
   * @param editEmail dto that contains the edited emails
   * @returns edited user
   */
  updateEmail(editEmail: UpdateUser): Observable<User> {
    return this.http.patch<User>(this.customersBaseUri, editEmail);
  }

  /**
   * Update the address of the user in the backend.
   *
   * @param editAddress dto that contains the edited address
   * @returns edited user
   */
  updateAddress(editAddress: UpdateUser): Observable<User> {
    return this.http.patch<User>(this.customersBaseUri, editAddress);
  }

  /**
   * Update the password of the user in the backend.
   *
   * @param editPassword dto that contains the new password
   * @returns true if the password has been changed successfully and false if not
   */
  updatePassword(editPassword: UpdateUser): Observable<User> {
    return this.http.patch<User>(this.customersBaseUri, editPassword);
  }

  /**
   * Checks whether the given passwords matches the password stored for the user in the database.
   *
   * @param checkPassword password to check
   * @returns true if the given and the persisted password match, false if not.
   */
  checkIfPasswordMatches(checkPassword: UpdatePassword): Observable<boolean> {
    return this.http.post<boolean>(this.customersBaseUri + '/password', checkPassword);
  }

  /**
   * Register a new user.
   *
   * @param userRegistration The DTO containing the user information
   * @returns An observable containing the newly registered user.
   */
  registerUser(userRegistration: UserRegistration): Observable<User> {
    return this.http.post<User>(this.customersBaseUri, userRegistration);
  }


  /**
   * Deletes the user.
   *
   * @returns observable that ce be subscribed to, to perform the request, but that
   * does not deliver any data
   */
  deleteUser(): Observable<void> {
    return this.http.delete<void>(this.customersBaseUri);
  }

  /**
   * Request a password reset email for a user.
   *
   * @param username The username of the user.
   */
  requestResetEmail(username: string): Observable<void> {
    return this.http.post<void>(this.customersBaseUri + '/requestReset/' + username, null);
  }

  /**
   * Reset the password of a user
   *
   * @param resetPassword A DTO containing the new password and the token of the reset link.
   */
  resetPassword(resetPassword: ResetPassword): Observable<void> {
    return this.http.put<void>(this.customersBaseUri + '/resetPassword', resetPassword);
  }

  /**
   * Indicates whether a reset token is valid.
   *
   * @param token The reset token to check.
   */
  isTokenValid(token: string): Observable<void> {
    const params = new HttpParams().set('token', token);
    return this.http.get<void>(this.customersBaseUri + '/token', {params});
  }
}
