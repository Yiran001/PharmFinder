import {Component, OnInit} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {AuthService} from "../services/auth.service";
import {TokenStorageService} from "../services/token-storage.service";
import {Router} from "@angular/router";
import {User} from "../user";
import {ProfileService} from "../services/profile.service";


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
  userList: Array<User> = [];
  userTemp: User | undefined;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService, private router: Router, private profileService: ProfileService) {

  }

  ngOnInit(): void {
    if (this.tokenStorage.getToken() != null) {
      this.router.navigate(['/home']).then();
    }

  }

  onSubmit(): void {

    const {username, password} = this.form;
    this.login(username, password);
  }

  public login(username: string, password: string) {
    this.authService.login(username, password).subscribe(
      data => {
        this.tokenStorage.saveToken(data.token);
        console.log(data);
        this.tokenStorage.saveUser(username);
        this.isLoginFailed = false;
        this.isSuccessful = true;
        this.reload();
      },
      error => {
        console.log('error' + error);
        this.isLoginFailed = true;

        if (error instanceof HttpErrorResponse) {
          this.errorMessage = error.error.message;
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
    this.profileService.getUsers().subscribe(
      data => {
        window.sessionStorage.setItem("IS_PHARMACIST", String(data[0].pharmacist));
      });
  }

  public reload() {
    window.location.reload();
  }


}
