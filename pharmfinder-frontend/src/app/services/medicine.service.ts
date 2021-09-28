import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

export interface Medicine {
  pzn: string;
  friendlyName: string;
  medicineForm: string;
}

@Injectable({
  providedIn: 'any'
})

export class MedicineService {

  constructor(private http: HttpClient) {
  }

  getMedicines() {
    return this.http.get<Map<Medicine, number>>(environment.baseUrl + "medicines/index?username=abc");
  }

}
