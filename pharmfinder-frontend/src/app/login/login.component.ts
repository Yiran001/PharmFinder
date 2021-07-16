import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {AuthService} from "../services/auth.service";
import {TokenStorageService} from "../services/token-storage.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  //template driven form
  form: any = {
    username: null,
    password: null,
  };
  isSuccessful = false;
  isLoginFailed = false;
  errorMessage = '';
  isLoggedIn = false;



  constructor(private authService: AuthService,private tokenStorage: TokenStorageService,private router:Router) {

  }
  ngOnInit(): void {

  }

  onSubmit(): void {

    const { username, password } = this.form;

    this.authService.login(username, password ).subscribe(
      data => {
        console.log(data);
        this.tokenStorage.saveToken(data.token);
        this.tokenStorage.saveUser(username);
        console.log(this.tokenStorage.getToken());
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.isSuccessful = true;
        this.router.navigate(['/home']);

      },
      error => {
        console.log(error)
        this.errorMessage = error.error.message;
        this.isLoginFailed = true;


        if (error instanceof HttpErrorResponse) {
          if (error.error instanceof ErrorEvent) {
            console.error("Error Event");
          } else {
            console.log(`error status : ${error.status} ${error.statusText}`);
            switch (error.status) {
              case 401:      //login

                break;
              case 403:     //forbidden
                console.error("access denied")
                break;
              case 200:
                this.isLoginFailed=false;
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
  reloadPage(): void {
    window.location.reload();
  }

}
