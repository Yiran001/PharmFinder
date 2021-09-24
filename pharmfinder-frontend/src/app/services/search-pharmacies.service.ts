import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";


var geocoder;
var map;
@Injectable({
  providedIn: 'root'
})

export class SearchPharmaciesService {
  geocoder = new google.maps.Geocoder() ;
  constructor(private http: HttpClient) { }

  geocodeAddress(address: string,callbackLng: any,callbackLat:any,callbackStatus: any){
    var result;
    this.geocoder.geocode({ 'address': address},function(results, status){
      if (status == 'OK') {
        result=results[0].geometry.location;
        callbackLng(results[0].geometry.location.lng());
        callbackLat(results[0].geometry.location.lat())
      } else {
          callbackStatus(status)
      }
    });
  }

  searchPharmacy(pzn: number, lat: String, lng: String): Observable<any>  {

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };
    return this.http.post(environment.baseUrl+'users/create', {
    }, httpOptions).pipe(
    );


  }
}
