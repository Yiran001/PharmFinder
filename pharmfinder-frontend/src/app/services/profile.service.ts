import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

export interface User {
  userID: number;
  username: string;
  email: string;
  pharmacist: boolean;
}

@Injectable({
  providedIn: 'any'
})
export class ProfileService {

  constructor(private http: HttpClient) { }

  getUsers() {
    return this.http.get('http://localhost:8080/users/index').subscribe(responseData => console.log(responseData));
  }
}
