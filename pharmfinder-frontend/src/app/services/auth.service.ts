import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, tap} from "rxjs/operators";

const AUTH_API = 'http://localhost:8083/users/register';
const AUTH_TEST = 'http://localhost:5000/user/register';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  registerUrl=AUTH_TEST;
  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password
    }, );
  }

  register(username: string, email: string, isPharmacist: boolean, password: string, addressStreet: string, addressHouseNumber: string, addressPostcode: string): Observable<any> {

    const parameters = new HttpParams().set("username",username).set("email", email).set("isPharmacist",isPharmacist).set("passwordHash",password).set("addressStreet",addressStreet).set("addressHouseNumber",addressHouseNumber).set("addressPostcode",addressPostcode); //Create new HttpParams

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
        //'Content-Type': 'application/json',
        //responseType:'text'
      }),
      params: parameters,
    };
    return this.http.put(AUTH_TEST, {
    }, httpOptions).pipe(
    );
  }
  registerPost(username: string, email: string, isPharmacist: boolean, password: string, addressStreet: string, addressHouseNumber: string, addressPostcode: string): Observable<any> {

    const parameters = new HttpParams().set("username",username).set("email", email).set("isPharmacist",isPharmacist).set("password",password).set("addressStreet",addressStreet).set("addressHouseNumber",addressHouseNumber).set("addressPostcode",addressPostcode); //Create new HttpParams

    const httpOptions = {
      headers: new HttpHeaders({
        //'Content-Type': 'application/x-www-form-urlencoded',
        'Content-Type': 'application/json',
        responseType:'text'
      }),
      params: parameters,
    };
    return this.http.post(AUTH_API, {
    }, httpOptions).pipe(
    );
  }

  //testing purposes with application/json
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
