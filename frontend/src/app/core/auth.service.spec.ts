import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { email } from '@angular/forms/signals';

describe('AuthService', () => {
  const user = { id: 1, username: 'vincent', email: 'vincent@test.com' };
  let service: AuthService;
  let http: HttpTestingController;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  }

  beforeEach(() => localStorage.clear());
  afterEach(() => http.verify());

  it("restaure l'utilisateur depuis le localstorage", () => {
    localStorage.setItem('pmt_user', JSON.stringify(user));
    setup();
    expect(service.user()).toEqual(user);
    expect(service.isLoggedIn()).toBe(true);
  });

  it("envoie de l'inscription à l'api", () => {
    setup();
    const request = { username: 'vincent', email: 'vincent@test.com', password: 'motdepasse123' };
  });

  it("connecte l'inscription et le garde", () => {
    setup();
    service.login({ email: 'vincent@test.com', password: 'motdepasse123' }).subscribe();
    http.expectOne('/api/auth/login').flush(user);
    expect(service.user()).toEqual(user);
    expect(localStorage.getItem('pmt_user')).toBe(JSON.stringify(user));
  });

  it("déconnecte l'utilisateur", () => {
    localStorage.setItem('pmt_user', JSON.stringify(user));
    setup();
    service.logout();
    expect(service.user()).toBeNull();
    expect(localStorage.getItem('pmt_user')).toBeNull();
  });
});
