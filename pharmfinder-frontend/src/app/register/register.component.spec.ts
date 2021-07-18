import {ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import {FormsModule} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {RouterTestingModule} from "@angular/router/testing";
import {Observable, of, throwError} from "rxjs";
import {HttpResponse} from "@angular/common/http";
import {User} from "../user";


describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let userServiceStub: Partial<AuthService>;
  let username='Max';
  let longUsername='MaxMustermann';
  let password='123456';
  let email='abd@gmx.de';
  let street='Musterstrasse';
  let number='1';
  let postcode='12345';
  /**
   * when username longer than 10 chars error returned for testing purposes
   */
  userServiceStub = {
    registerPost(username: string,email:string, isPharmacist:boolean,password: string,addressStreet: string, addressHouseNumber: string, addressPostcode: string): Observable<any> {
      let data= 'saved'
      const expectedResponse = new HttpResponse({ status: 200, statusText: 'saved',  });
      const obsof2=of(data);
      if(username.length>10) {
        return throwError(expectedResponse);
      }else{
        return obsof2;
      }
    },
  };
  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports:[RouterTestingModule,FormsModule],
      providers: [
        { provide: AuthService, useValue: userServiceStub }
      ],
      declarations: [ RegisterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.isSuccessful=false;
    component.isSignUpFailed=false;
  });


  it('should be created', () => {
    expect(component).toBeTruthy();
  });
  it('signUp sucessful when saved returned as observable', () => {
    expect(component.isSuccessful).toBeFalse();
    component.form.username=username;
    component.form.password=password;
    component.form.email=email;
    component.form.housenumber=number;
    component.form.street=street;
    component.form.postcode=postcode;
    component.form.isPharmacist=false;
    component.onSubmit();
    fixture.detectChanges();
    expect(component.isSuccessful).toBeTrue();
    expect(component.isSignUpFailed).toBeFalse();
  });
  it('signUp failed when observable is error', () => {
    expect(component.isSuccessful).toBeFalse();
    component.form.username=longUsername;
    component.form.password=password;
    component.form.email=email;
    component.form.housenumber=number;
    component.form.street=street;
    component.form.postcode=postcode;
    component.form.isPharmacist=false;
    expect(component.isSuccessful).toBeFalse();
    component.onSubmit();
    fixture.detectChanges();
    expect(component.isSuccessful).toBeFalse();
    expect(component.isSignUpFailed).toBeTrue();
  });

});
