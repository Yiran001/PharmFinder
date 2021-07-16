import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, tap} from "rxjs/operators";

const DEV_URL = 'http://localhost:8080/users/create';
const AUTH_TEST = 'http://localhost:5000/user/register';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  registerUrl=AUTH_TEST;
  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post('' + 'signin', {
      username,
      password
    }, );
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
    return this.http.post(DEV_URL, {
    }, options).pipe(
    );
  }

  register2(): Observable<any> {

    const parameters = new HttpParams().set("pzn","1").set("friendlyName", "Paracetamol").set("medicineForm","flüssig").set("username","justin"); //Create new HttpParams

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
        //'Content-Type': 'application/json',
      }),
      params: parameters,
    };

    return this.http.post(AUTH_TEST, {

    }, httpOptions);
  }

}
