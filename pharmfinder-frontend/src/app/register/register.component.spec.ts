import {ComponentFixture, fakeAsync, TestBed,async} from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import {FormsModule} from "@angular/forms";


describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterComponent],
      imports: [FormsModule]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
  it('should be created', () => {
    expect(component).toBeTruthy();
  });
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
