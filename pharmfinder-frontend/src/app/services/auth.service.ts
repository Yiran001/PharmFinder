import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, tap} from "rxjs/operators";

const DEV_URL_REGISTER = 'http://localhost:8083/users/create';
const DEV_URL_LOGIN = 'http://localhost:8083/authenticate';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  registerUrl=DEV_URL_REGISTER;
  constructor(private http: HttpClient) { }


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

    return this.http.post(DEV_URL_LOGIN, user,httpOptions );

  }

  registerPost(username: string, email: string, isPharmacist: boolean, password: string, addressStreet: string, addressHouseNumber: string, addressPostcode: string): Observable<any> {

    const parameters = new HttpParams().set("username",username).set("email", email).set("isPharmacist",isPharmacist).set("password",password).set("addressStreet",addressStreet).set("addressHouseNumber",addressHouseNumber).set("addressPostcode",addressPostcode); //Create new HttpParams

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
        //'Content-Type': 'application/x-www-form-urlencoded',
        'Content-Type': 'application/json',
      }),
      params: parameters,
      responseType: 'text'
    };
    return this.http.post(DEV_URL_REGISTER, {
    }, options).pipe(
    );
  }

}
