import {Component, OnInit, ViewChild} from '@angular/core';
import { AuthService } from '../services/auth.service';
import {Observable, throwError} from "rxjs";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from "@angular/common/http";
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";

/**
 * This component binds form data (username, email, password) from template to
 * AuthService.register() method that returns an Observable object
 * Form Validation: username: required, minLength=3, maxLength=20
 * email: required, email format
 * password: required, minLength=6
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  //@ViewChild('form',{static: true}) form2: NgForm;
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
  errorCode: any = null;

  constructor(private authService: AuthService,private router: Router) { }


  ngOnInit(): void {
  }

  onSubmit(): void {
    const { username, email, password, street, housenumber, postcode, isPharmacist } = this.form;

    this.authService.registerPost(username, email, isPharmacist, password, street,housenumber,postcode).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['/login']);

      },
      error => {
        console.log(error)
        this.errorMessage = error.error.message;
        this.isSignUpFailed = true;
        this.usernameAlreadyGiven=false;

        if (error instanceof HttpErrorResponse) {
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
              case 200:
                console.log('hi');
                this.isSignUpFailed=false;
                this.isSuccessful=true;
                break;
            }
          }
        } else {
          console.error("some thing else happened");
        }
        return throwError(error);
      }
    );
  }
}
