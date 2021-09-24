import {Component, OnInit, ViewChild} from '@angular/core';
import {GoogleMap} from "@angular/google-maps";
import {SearchPharmaciesService} from "../services/search-pharmacies.service";

@Component({
  selector: 'app-search-pharmacies',
  templateUrl: './search-pharmacies.component.html',
  styleUrls: ['./search-pharmacies.component.css']
})
export class SearchPharmaciesComponent implements OnInit {


  center: google.maps.LatLngLiteral = { lat: 52.52, lng: 13.4 };
  mapOptions: google.maps.MapOptions = {
    zoom : 14,
    streetViewControl: false,
    mapTypeControl: false,

  }
  currentLocCenter: google.maps.LatLngLiteral = { lat: 52.52, lng: 13.4 };
  currentLocMarker = {
    position: this.currentLocCenter,
    label: {
      color: 'black',
      text: 'S',
    },

  }
  form: any = {
    pzn: null,
  };
  isSuccessful = false;
  foundMe = false;


  // @ts-ignore
  @ViewChild(GoogleMap, { static: false }) map: GoogleMap;

  logCenter() {
    console.log(JSON.stringify(this.map.getCenter()))
  }

  constructor(private searchPharmService : SearchPharmaciesService) {  }

  ngOnInit(): void {
    //const geocoder = new google.maps.Geocoder();
    navigator.geolocation.getCurrentPosition((position) => {
      this.currentLocCenter = {
        lat: position.coords.latitude,
        lng: position.coords.longitude,
      }
    });
  }

  onSubmit() {
    const { pzn } = this.form;
    this.foundMe=true;
    this.center=this.currentLocCenter;
    this.currentLocMarker.position=this.center;
    //console.log(JSON.stringify(this.map.getZoom()));
    this.search(pzn,this.currentLocMarker.position);
  }
  /*
  geocodeAddress(
    geocoder: google.maps.Geocoder,
    resultsMap: google.maps.Map
  ) {
    const address = (document.getElementById("address") as HTMLInputElement)
      .value;
    geocoder
      .geocode({ address: address })
      .then(({ results }) => {
        resultsMap.setCenter(results[0].geometry.location);
        new google.maps.Marker({
          map: resultsMap,
          position: results[0].geometry.location,
        });
      })
      .catch((e: any) =>
        alert("Geocode was not successful for the following reason: " + e)
      );
  }


  click(event: google.maps.MouseEvent) {
    console.log(event)
  }
   */

  private search(pzn: number, location: google.maps.LatLngLiteral) {
    this.searchPharmService.searchPharmacy(pzn,location.lat.toString(),location.lng.toString());
  }

  findMe() {
    this.foundMe=true;
    this.center=this.currentLocCenter;
    this.currentLocMarker.position=this.center;
  }


}
