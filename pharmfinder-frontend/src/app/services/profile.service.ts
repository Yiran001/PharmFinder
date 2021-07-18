import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {tap} from 'rxjs/operators';

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
    return this.http.get<User[]>('http://localhost:8080/users/index').pipe(
      tap(_ => console.log(_)));
  }

  getAddresses() {
    return this.http.get<Address[]>('http://localhost:8080/addresses/index').pipe(
      tap(_ => console.log(_)));
  }
}
