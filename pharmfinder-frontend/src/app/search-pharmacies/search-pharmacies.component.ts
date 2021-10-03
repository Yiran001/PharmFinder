import {Component, OnInit, ViewChild} from '@angular/core';
import {GoogleMap, MapInfoWindow, MapMarker} from "@angular/google-maps";
import {SearchPharmaciesService} from "../services/search-pharmacies.service";
import {foundPharmacy} from "../foundPharmacy";
import {HttpErrorResponse} from "@angular/common/http";
import {AppComponent} from "../app.component";

export interface marker{
  position: google.maps.LatLngLiteral;
  label: google.maps.MarkerLabel;
  title: string;
  address: string;
  dist: string;
}

@Component({
  selector: 'app-search-pharmacies',
  templateUrl: './search-pharmacies.component.html',
  styleUrls: ['./search-pharmacies.component.css']
})
export class SearchPharmaciesComponent implements OnInit {

  medicineNotFound=false;
  otherErrorOccurred=false;
  markers: Array<marker> =[];
  foundPharms: Array<foundPharmacy> = [];
  markerAddress: string='';
  markerDist: string='';
  errorMessage='Suche des Medikamentes fehlgeschlagen';

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

  // @ts-ignore
  @ViewChild(MapInfoWindow, { static: false }) infoWindow: MapInfoWindow
  // @ts-ignore
  titleS: string= 'Standort';
  openInfo(marker: MapMarker, address: string, dist: string) {
      this.markerAddress=address;
      this.markerDist=dist;
      this.infoWindow.open(marker)
  }
  openInfoCurrentLocation(markerLoc: MapMarker) {
      this.markerAddress='Ihr Standort';
      this.markerDist=''
      this.infoWindow.open(markerLoc);
  }

  constructor(private searchPharmService : SearchPharmaciesService,private app: AppComponent) {  }

  ngOnInit(): void {
    this.searchPosition();

  }
  searchPosition(){
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
    this.search(pzn,this.currentLocMarker.position);
  }

  async search(pzn: number, location: google.maps.LatLngLiteral): Promise<void> {
    this.markers=[];
    this.foundPharms=await this.searchPharmService.searchPharmacy
    (pzn.toString(),location.lat.toString(),location.lng.toString()).toPromise()
      .catch((err: HttpErrorResponse)=>{
      if(err.status==404){
          this.medicineNotFound=true;
      }else if(err.status==401){
        this.app.logout();
      }else
        this.otherErrorOccurred=true;
    });
    if(this.foundPharms!=undefined) {
      this.foundPharms.forEach((element) => {
        this.addMarker(element.latitude, element.longitude, element.street
          + ' ' + element.houseNumber + ',' + element.postcode, element.dist)


      });
    }
  }

  private addMarker(latitude: string,longitude: string,address:string,distance:string) {
    var i=distance.indexOf('.');
    var d1= distance.substr(0,i);
    var d2=distance.substr(i,i+1)
    this.markers.push({
      position: {
        lat: Number(latitude),
        lng: Number(longitude),
      },
      label: {
        color: 'black',
        text: 'A' + (this.markers.length + 1),
      },
      title: 'Apotheke '+(this.markers.length + 1),
      address: 'Adresse: '+address+'         ',
      dist: 'Entfernung: '+d1+d2+' km',

    });
  }

  findMe() {
    this.searchPosition();
    this.foundMe=true;
    this.center=this.currentLocCenter;
    this.currentLocMarker.position=this.center;
  }
}
