import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import { LoginComponent } from './login.component';
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {Observable, of, pipe, throwError} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let userServiceStub: Partial<AuthService>;
  let username='Max';
  let longUsername='MaxMustermann';
  let password='123456'
  /**
   * mock of auth.service
   * returns error when username longer than 10 characters for testing purposes only!!
   */
  userServiceStub = {
    login(username: string, password: string): Observable<any> {
      let data= 'token : fewfwifwfew.cwcqc.qcewqcf.fbdsbb.trbeterbrebnlf'
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
      declarations: [ LoginComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.isSuccessful=false;
    component.isLoginFailed=false;

  });

  it('should create',  () => {
    expect(component).toBeTruthy();
  });

  
   it('user is logged in when calling login()', fakeAsync(() => {
     expect(component.isSuccessful).toBeFalse();
     let spy= spyOn(component, 'reload');
     component.login(username,password);
     fixture.detectChanges();
     expect(spy).toHaveBeenCalled();
     expect(component.isSuccessful).toBeTrue();
     expect(component.isLoginFailed).toBeFalse();
   }));

  it('login error occured -> loginFailed=true, isSucessful=false', () => {
    expect(component.isSuccessful).toBeFalse();
    component.login(longUsername,password);
    fixture.detectChanges();
    expect(component.isSuccessful).toBeFalse();
    expect(component.isLoginFailed).toBeTrue();
  });
});
