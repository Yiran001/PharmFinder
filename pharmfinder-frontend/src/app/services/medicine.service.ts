import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

export interface Medicine {
  pzn: string;
  friendlyName: string;
  medicineForm: string;
  amount: number;
}

const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'any'
})

export class MedicineService {

  constructor(private http: HttpClient) {
  }

  getMedicines() {
    let user = window.sessionStorage.getItem(USER_KEY)

    return this.http.get<Medicine[]>(environment.baseUrl + "medicines/index?username=" + user);
  }

  getMedicinesFiltered(filterCategory: string, filterData: string): Observable<Medicine[]> {
    let user = window.sessionStorage.getItem(USER_KEY)

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };

    return this.http.get<Medicine[]>(environment.baseUrl + "search_and_filter/get", httpOptions);
  }

  updateMedicines(medicine: Medicine): Observable<Medicine> | null {
    let user = window.sessionStorage.getItem(USER_KEY)
    if (!!user) {
      const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", user).set("amount", medicine.amount);
      const req = this.http.put<Medicine>(environment.baseUrl + "medicines/update", parameters);
      req.subscribe();
      return req;
    }
    return null
  }

  createMedicine(medicine: Medicine): Observable<Medicine> | null {
    let user = window.sessionStorage.getItem(USER_KEY)
    if (!!user) {
      const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", user).set("amount", medicine.amount);
      const req = this.http.post<Medicine>(environment.baseUrl + "medicines/create", parameters);
      req.subscribe();
      return req;
    }
    return null

  }

  deleteMedicines(medicine: Medicine): Observable<Medicine> | null {
    let user = window.sessionStorage.getItem(USER_KEY)
    const req = this.http.delete<Medicine>(environment.baseUrl + "medicines/delete?username=" + user + "&pzn=" + medicine.pzn);
    req.subscribe();
    return req;
  }
}
