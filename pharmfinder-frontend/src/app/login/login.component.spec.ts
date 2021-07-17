import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import { LoginComponent } from './login.component';
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {Observable} from "rxjs";
import {HttpHeaders} from "@angular/common/http";

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  class MockAuthService {
    login(username: string, password: string) {
      let user = {
        username: username,
        password: password
      }
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
      };

      return ;

    }
  }
  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports:[RouterTestingModule,FormsModule],
      providers: [
        { provide: AuthService, useValue: MockAuthService }
      ],
      declarations: [ LoginComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  /**
  it('testing login', () => {
    component.form.username='Max';
    component.form.password='123456';
    console.log(component.form.username)
    component.onSubmit()
  });

  it('[Email - Check - Invalid] Should check email field is not valid',async(()=>{
    fixture.whenStable().then(()=>{
      let email = component.form.username;
      email.setValue('abc');
      console.log(component.form.username)
      expect(email.valid).toBeFalsy();
      expect(component.form.valid).toBeFalsy();
      email.setValue('abc');
      expect(email.errors['email']).toBeTruthy();
    });

  }));
  it('should update the favorite color on the input field', fakeAsync(() => {
    component.form.username = 'Blue';
    console.log(component.form.username)

    fixture.detectChanges();
    tick();
    const input = fixture.nativeElement.querySelector('input');

    expect(input.value).toBe('Blue');
  }));
  it('should update the favorite color in the component', fakeAsync(() => {
    const input = fixture.nativeElement.querySelector('input');

    input.value = 'Red';
    input.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.form.username).toEqual('Red');
  }));
   */
});
