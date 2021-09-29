import {Component, OnInit, ViewChild} from '@angular/core';
import {GoogleMap, MapInfoWindow, MapMarker} from "@angular/google-maps";
import {SearchPharmaciesService} from "../services/search-pharmacies.service";
import {foundPharmacy} from "../foundPharmacy";

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

  markers: Array<marker> =[];
  foundPharms: Array<foundPharmacy> = [];
  foundPharm: foundPharmacy | undefined;
  markerAddress: string='';
  markerDist: string='';

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
  // @ts-ignore
  @ViewChild(MapInfoWindow, { static: false }) infoWindow: MapInfoWindow
  // @ts-ignore
  openInfo(marker: MapMarker, address: string, dist: string) {
      this.markerAddress=address;
      this.markerDist=dist;
      this.infoWindow.open(marker)
  }

  constructor(private searchPharmService : SearchPharmaciesService) {  }

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
    this.foundPharms=await this.searchPharmService.searchPharmacy(pzn.toString(),location.lat.toString(),location.lng.toString()).toPromise();
    console.log(this.foundPharms);
    this.foundPharms.forEach( (element)=>{
      this.addMarker(element.latitude,element.longitude,element.street+' '+element.houseNumber+','+element.postcode,element.dist)
    });
    console.log(this.markers);
  }

  private addMarker(latitude: string,longitude: string,address:string,distance:string) {
    this.markers.push({
      position: {
        lat: Number(latitude),
        lng: Number(longitude),
      },
      label: {
        color: 'black',
        text: 'A' + (this.markers.length + 1),
      },
      title: 'Marker title ' + (this.markers.length + 1),
      address: 'Adresse: '+address+'         ',
      dist: 'Entfernung: '+distance,

    })
  }

  findMe() {
    this.searchPosition();
    this.foundMe=true;
    this.center=this.currentLocCenter;
    this.currentLocMarker.position=this.center;
  }


}
