
import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from "@angular/common/http";
import {User} from "../user";


describe('#AuthService.register()', () => {
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
  fdescribe('important test',()=>{
    it("should register user and return username",() => {
        const user: User = {
          username: "Max",
          email: "maxmusterman@gmail.com",
          isPharmacist: false,
          passwordHash: "123456",
          addressStreet: "Musterstraße",
          addressHouseNumber: "1",
          addressPostcode: "1111"
        }

        const parameters = new HttpParams().set("username",user.username).set("email", user.email).set("isPharmacist",user.isPharmacist).set("passwordHash",user.passwordHash).set("addressStreet",user.addressStreet).set("addressHouseNumber",user.addressHouseNumber).set("addressPostcode",user.addressPostcode); //Create new HttpParams

        const httpOptions = {
          headers: new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded',
            //'Content-Type': 'application/json',
            responseType:'text'
          }),
          params: parameters,
        };

        authService.register(user.username,user.email,user.isPharmacist,user.passwordHash,user.addressStreet,user.addressHouseNumber,user.addressPostcode).subscribe(
          data=> expect(data).toEqual(user.username, 'should return the observable'),
          fail
        );

        //register should have made one request to PUT user
        const req = httpTestingController.expectOne(authService.registerUrl+'?'+httpOptions.params);
        expect(req.request.method).toEqual('PUT');
        expect(req.request.body).toEqual({});
        expect(req.request.params).toEqual(httpOptions.params);

        //const expectedResponse = new HttpResponse({ status: 200, statusText: 'saved',  });
        //req.event(expectedResponse);
        //req.flush({ status: 200, statusText: 'Not Found' });
      }
    );});


});



