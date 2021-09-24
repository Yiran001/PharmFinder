import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";

export interface User {
  userID: any;
  username: string;
  email: string;
  pharmacist: boolean;
}

export interface Address {
  addressID: any;
  addressUsers: User[];
  street: string;
  houseNumber: string;
  postcode: string;
}

@Injectable({
  providedIn: 'any'
})
export class ProfileService {

  constructor(private http: HttpClient) {
  }

  getUsers() {
    /*
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };

     */
    return this.http.get<User[]>(environment.baseUrl + "users/index");
  }

  getAddresses() {
    return this.http.get<Address[]>(environment.baseUrl + "addresses/index");
  }
}
