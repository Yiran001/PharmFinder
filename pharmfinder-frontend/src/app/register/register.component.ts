import {Component, OnInit, ViewChild} from '@angular/core';
import { AuthService } from '../services/auth.service';
import {Observable, throwError} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";
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
        this.router.navigate(['/login']).then();

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
