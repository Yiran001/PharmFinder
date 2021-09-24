import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";

const DEV_URL_REGISTER = 'http://localhost:8080/users/create';
const DEV_URL_LOGIN = 'http://localhost:8080/authenticate';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  registerUrl=DEV_URL_REGISTER;
  loginURL=DEV_URL_LOGIN;
  constructor(private http: HttpClient) { }

  /**
   * login as user
   * @param username
   * @param password
   */
  login(username: string, password: string): Observable<any> {
    let user = {
      username: username,
      password: password
    }
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };
    return this.http.post(environment.baseUrl+'authenticate', user,httpOptions );
  }

  /**
   * register user
   * @param username
   * @param email
   * @param isPharmacist
   * @param password
   * @param addressStreet
   * @param addressHouseNumber
   * @param addressPostcode
   */
  registerPost(username: string, email: string, isPharmacist: boolean, password: string, addressStreet: string, addressHouseNumber: string, addressPostcode: string, latitude: string, longitude: string): Observable<any> {

    const parameters = new HttpParams().set("username",username).set("email", email).set("isPharmacist",isPharmacist).set("password",password).set("addressStreet",addressStreet).set("addressHouseNumber",addressHouseNumber).set("addressPostcode",addressPostcode).set("latitude",latitude).set("longitude",longitude); //Create new HttpParams

    //angular expects json by default -> work around for text return
    const options: {
      headers?: HttpHeaders,
      observe?: 'body',
      params?: HttpParams,
      reportProgress?: boolean,
      responseType: 'text',
      withCredentials?: boolean
    } = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      params: parameters,
      responseType: 'text'
    };
    return this.http.post(environment.baseUrl+'users/create', {
    }, options).pipe(
    );
  }

}
