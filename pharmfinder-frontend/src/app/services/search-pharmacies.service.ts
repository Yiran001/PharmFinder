import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";


var geocoder;
var map;
@Injectable({
  providedIn: 'root'
})

export class SearchPharmaciesService {
  geocoder = new google.maps.Geocoder() ;
  constructor() { }

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
}
