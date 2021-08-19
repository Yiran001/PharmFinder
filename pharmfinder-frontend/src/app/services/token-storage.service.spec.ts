import { TestBed } from '@angular/core/testing';

import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenStorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('good: username of user saved with saveUser() in window.sessionStorage retrieved with getUser()', () => {
    let testToken ='eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c'
    service.saveToken(testToken);
    let token = service.getToken()
    expect(token).toEqual(testToken);
  });
  it('good: token saved in windowStorage by saveToken() and returned by getToken()', () => {
    let testUsername ='MaxMustermann2000'
    service.saveUser(testUsername);
    let user = service.getUser()
    expect(user).toEqual(testUsername);
  });
  it('good: signOut() should clear user and token from windowStorage', () => {
    let testUsername ='BerndDasBrot1000'
    service.saveUser(testUsername);
    let user = service.getUser()
    let testToken ='eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c'
    service.saveToken(testToken);
    let token = service.getToken()
    expect(token).toEqual(testToken);
    expect(user).toEqual(testUsername);
    service.signOut();
    let resultUsername=service.getUser();
    let resultToken=service.getToken();
    expect(resultUsername).toEqual({});
    expect(resultToken).toEqual(null);
  });
});
