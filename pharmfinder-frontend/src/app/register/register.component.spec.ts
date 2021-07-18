import {ComponentFixture, fakeAsync, TestBed,async} from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import {FormsModule} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {RouterTestingModule} from "@angular/router/testing";
import {LoginComponent} from "../login/login.component";


describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  class MockAuthService{

  };
  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports:[RouterTestingModule,FormsModule],
      providers: [
        { provide: AuthService, useValue: MockAuthService }
      ],
      declarations: [ RegisterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });


  it('should be created', () => {
    expect(component).toBeTruthy();
  });
  /**
  it('should update the favorite color in the component', fakeAsync(() => {
    const input = fixture.nativeElement.querySelector('input');
    //const event = createNewEvent('input');

    input.value = 'Red';
    input.dispatchEvent(event);

    fixture.detectChanges();

      //expect(component.favoriteColor).toEqual('Red');
  }));


  it('[Email - Check - Invalid] Should check email field is not valid',async(()=>{
    fixture.whenStable().then(()=>{
      let email = component.form.form.controls['email'];
      expect(email.valid).toBeFalsy();
      expect(component.form.valid).toBeFalsy();
      email.setValue('abc');
      expect(email.errors['email']).toBeTruthy();
    });

  }));
*/
});
