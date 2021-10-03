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

const DEV_URL_CREATE = 'http://localhost:8080/medicines/create';
const DEV_URL_GET = 'http://localhost:8080/medicines/index';
const DEV_URL_DELETE = 'http://localhost:8080/medicines/delete';
const DEV_URL_UPDATE = 'http://localhost:8080/medicines/update';
const DEV_URL_SEARCH_AND_FILTER_GET = 'http://localhost:8080/search_and_filter/get';
const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'any'
})

export class MedicineService {
  createURL = DEV_URL_CREATE;
  getURL = DEV_URL_GET;
  deleteURL = DEV_URL_DELETE;
  updateURL = DEV_URL_UPDATE;
  searchAndFilterGetURL = DEV_URL_SEARCH_AND_FILTER_GET;


  constructor(private http: HttpClient) {
  }

  getMedicines() {
    let user = window.sessionStorage.getItem(USER_KEY)

    return this.http.get<Medicine[]>(environment.baseUrl + "medicines/index?username=" + user);
  }

  getMedicinesFiltered(filterCategory: string, filterData: string) {
    let user = window.sessionStorage.getItem(USER_KEY)

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };

    switch (filterCategory) {
      case "pzn":
        return this.http.get<Medicine[]>(environment.baseUrl + "search_and_filter/get?username=" + user + "&" + filterCategory + "=" + filterData);
        break
      case "friendlyName":
        return this.http.get<Medicine[]>(environment.baseUrl + "search_and_filter/get?username=" + user + "&" + filterCategory + "=" + filterData);
        break
      case "medicineForm":
        return this.http.get<Medicine[]>(environment.baseUrl + "search_and_filter/get?username=" + user + "&" + filterCategory + "=" + filterData);
        break
      case "amount":
        return this.http.get<Medicine[]>(environment.baseUrl + "search_and_filter/get?username=" + user + "&" + filterCategory + "=" + filterData);
        break
      default:
        return this.http.get<Medicine[]>(environment.baseUrl + "medicines/index?username=" + user);
        break


    }
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
      return this.http.post<Medicine>(environment.baseUrl + "medicines/create", parameters);
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
