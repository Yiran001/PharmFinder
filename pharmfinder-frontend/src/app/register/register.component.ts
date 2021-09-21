import {Component, OnInit, ViewChild} from '@angular/core';
import { AuthService } from '../services/auth.service';
import {Observable, throwError} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";
import {Router} from "@angular/router";
import {SearchPharmaciesService} from "../services/search-pharmacies.service";

/**
 * This component binds form data (username, email, password) from template to
 * AuthService.register() method that returns an Observable object
 *
 * Form Validation:
 * username: required, minLength=3, maxLength=20
 * email: required, email format
 * password: required, minLength=6
 * housenumber: max="9999", required,pattern="^\d+$"
 * street: required, pattern="^[a-zA-Zß]*$"
 * postcode: max="9999", required, pattern="^\d+$"
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {


  form: any = {
    username: null,
    email: null,
    password: null,
    street: null,
    housenumber: null,
    postcode: null,
    isPharmacist: false
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  registerPharmacy = false;
  usernameAlreadyGiven = false;
  address:string = '';


  constructor(private authService: AuthService,private router: Router,private searchPharmaciesService: SearchPharmaciesService) { }


  ngOnInit(): void {
  }

  onSubmit(): void {
    var result;
    var lat=-200;
    var lng=-200;
    var status='OK';
    const { username, email, password, street, housenumber, postcode, isPharmacist } = this.form;
    this.address = this.form.street+' '+this.form.housenumber+' '+this.form.postcode;
    this.searchPharmaciesService.geocodeAddress(this.address,function(results: any){
      lng=results;
      console.log('lng: '+lng);
    },
      function(results: any){
        lat=results;
        console.log('lat: '+lat);
      }
      ,
      function(results: any){
        status=results;
        console.log('status: '+status);
        if(status!=='OK') {
          console.log(status);
          if(status=='ZERO_RESULTS') {
            alert('Adresse konnte nicht gefunden werden!');
          } else if (status=='OVER_QUERY_LIMIT'){
            alert('Adresse ist zu lang.');
          } else {
            alert('Ein Problem mit der Adresse ist aufgetreten. Bitte versuchen Sie erneut sich zu registrieren.')
          }
        }
      });




    this.authService.registerPost(username, email, isPharmacist, password, street,housenumber,postcode).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['/login']).then();

      },
      error => {
        console.log(error)

        this.isSignUpFailed = true;
        this.usernameAlreadyGiven=false;

        if (error instanceof HttpErrorResponse) {
          this.errorMessage = error.error.message;
          if (error.error instanceof ErrorEvent) {
            console.error("Error Event");
          } else {
            console.log(`error status : ${error.status} ${error.statusText}`);
            switch (error.status) {
              case 401:      //login

                break;
              case 403:     //forbidden

                break;
              case 409:     //username already taken
                this.usernameAlreadyGiven=true;
                console.log('Username vergeben')
                break;
            }
          }
        } else {
          console.error("undefined error status");
        }
        return throwError(error);
      }
    );
  }
}
