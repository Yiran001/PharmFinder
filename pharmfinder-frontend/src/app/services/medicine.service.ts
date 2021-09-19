import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

export interface Medicine {
  pzn: string;
  name: string;
  form: string;
  amount: number;
}

@Injectable({
  providedIn: 'any'
})

export class MedicineService {

  constructor(private http: HttpClient) {
  }

  getMedicines() {
    return this.http.get<Medicine[]>(environment.baseUrl + "/medicines/index");
  }

}
