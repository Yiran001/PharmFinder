import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import {TokenStorageService} from "./services/token-storage.service";
import {AuthService} from "./services/auth.service";

describe('AppComponent', () => {
  const TOKEN_KEY = 'auth-token';
  const USER_KEY = 'auth-user';


  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      providers: [

      ],
      declarations: [
        AppComponent
      ],

    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'myapp'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('Apothekenfinder');
  });
  it('isLoggedIn false when no token in sessionStorage or token deleted', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.isLoggedIn).toEqual(false);
  });
  it('isLoggedIn true when token saved in sessionStorage from tokenService', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.isLoggedIn).toEqual(false);
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsInVzZXIiOiJNYXhNdXN0ZXJtYW5uIn0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.yt-sUdZ21te5dvr_pdvNI6R7ZmAFk5JXRXI-H9YgZg0');
    fixture.detectChanges();
    expect(app.isLoggedIn).toEqual(true);
  });



});
