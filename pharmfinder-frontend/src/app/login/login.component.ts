import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {AuthService} from "../services/auth.service";
import {TokenStorageService} from "../services/token-storage.service";
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

//  @ViewChild('form',{static: true}) form: NgForm;

  //template driven form
  form: any = {
    username: null,
    password: null,
  };

  isSuccessful = false;
  isLoginFailed = false;
  errorMessage = '';


  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService, private router:Router) {

  }
  ngOnInit(): void {
    if(this.tokenStorage.getToken()!=null){
      this.router.navigate(['/home']).then();
    }

  }

  onSubmit(): void {

    const { username, password } = this.form;

    this.login(username,password);
  }
  public login(username:string,password:string){
    this.authService.login(username, password ).subscribe(
      data => {
        console.log(data);
        this.tokenStorage.saveToken(data.token);
        console.log(data.token);
        this.tokenStorage.saveUser(username);
        this.isLoginFailed = false;
        this.isSuccessful = true;
        this.reload();
      },
      error => {
        console.log('error'+error);
        this.isLoginFailed = true;
        this.errorMessage = error.error.message;
        if (error instanceof HttpErrorResponse) {
          if (error.error instanceof ErrorEvent) {
            console.error("Error Event");
          } else {
            console.log(`error status : ${error.status} ${error.statusText}`);
            switch (error.status) {
              case 401:      //login failed
                break;
              case 403:     //forbidden
                console.error("access denied")
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
  public reload(){
    window.location.reload();
  }


}
