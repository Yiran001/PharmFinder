import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

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

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(environment.baseUrl + "users/index");
  }

  getAddresses() {
    return this.http.get<Address[]>(environment.baseUrl + "addresses/index");
  }
}
