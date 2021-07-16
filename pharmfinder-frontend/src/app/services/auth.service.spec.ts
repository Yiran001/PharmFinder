
import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from "@angular/common/http";
import {User} from "../user";


describe('#AuthService', () => {
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let authService: AuthService;



  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers:
        [AuthService]
    });
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });


  let msg;
  it("Test description", function() {
    msg = 'Hello';
    expect(msg).toEqual('Hello');
  });

    it("good: should register user with POST and with valid http options  ",() => {
        const user: User = {
          username: "Max",
          email: "maxmusterman@gmail.com",
          isPharmacist: false,
          password: "123456",
          addressStreet: "Musterstraße",
          addressHouseNumber: "1",
          addressPostcode: "1111"
        }

        const parameters = new HttpParams().set("username",user.username).set("email", user.email).set("isPharmacist",user.isPharmacist).set("password",user.password).set("addressStreet",user.addressStreet).set("addressHouseNumber",user.addressHouseNumber).set("addressPostcode",user.addressPostcode); //Create new HttpParams


        const httpOptions = {
          headers: new HttpHeaders({
            //'Content-Type': 'application/x-www-form-urlencoded',
            'Content-Type': 'application/json',
            responseType:'text'
          }),
          params: parameters,
        };

        authService.registerPost(user.username,user.email,user.isPharmacist,user.password,user.addressStreet,user.addressHouseNumber,user.addressPostcode).subscribe(
          data=> expect(data).toEqual(null, 'should return the observable'),
          fail
        );


        //register should have made one request to POST user
        const req = httpTestingController.expectOne(authService.registerUrl+'?'+httpOptions.params);
        expect(req.request.method).toEqual('POST');
        expect(req.request.body).toEqual({});
        expect(req.request.params).toEqual(httpOptions.params);

        const expectedResponse = new HttpResponse({ status: 200, statusText: 'saved',  });
        req.event(expectedResponse);
        //req.flush({ status: 200, statusText: 'Not Found' });
      }
    );
});




