import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {foundPharmacy} from "../foundPharmacy";


var geocoder;
var map;

@Injectable({
  providedIn: 'root'
})

export class SearchPharmaciesService {
  geocoder = new google.maps.Geocoder();

  constructor(private http: HttpClient) {
  }


  geocodeAddress(address: string, callbackLng: any, callbackLat: any, callbackStatus: any) {
    var result;
    this.geocoder.geocode({'address': address},
      function (results, status) {
        if (status == 'OK') {
          result = results[0].geometry.location;
          callbackLng(results[0].geometry.location.lng());
          callbackLat(results[0].geometry.location.lat());
        } else {
          callbackStatus(status)
        }
      });
  }

  searchPharmacy(pzn: String, lat: String, lng: String): Observable<any> {
    // @ts-ignore
    const parameters = new HttpParams().set("pzn", pzn).set("latitude", lat).set("longitude", lng); //Create new HttpParams

    const options: {
      headers?: HttpHeaders,
      observe?: 'body',
      params?: HttpParams,
    } = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      params: parameters,
    };
    return this.http.get<foundPharmacy[]>(environment.baseUrl + 'search/pharmacy', options,);
  }
}
