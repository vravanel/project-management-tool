import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { Login } from './login';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let http: HttpTestingController;
  let navigate: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(Login);
    http = TestBed.inject(HttpTestingController);
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
  });

  afterEach(() => http.verify());

  it('redirige vers les projets après une connexion réussie', () => {
    fixture.componentInstance['email'] = 'vincent@test.com';
    fixture.componentInstance['password'] = 'motdepasse123';
    fixture.componentInstance['submit']();
    const call = http.expectOne('/api/auth/login');
    expect(call.request.body).toEqual({ email: 'vincent@test.com', password: 'motdepasse123' });
    call.flush({ id: 1, username: 'vincent', email: 'vincent@test.com' });
    expect(navigate).toHaveBeenCalledWith(['/projects']);
  });

  it('affiche un message quand les identifiants sont refusés', () => {
    fixture.componentInstance['submit']();
    http.expectOne('/api/auth/login').flush('refus', { status: 401, statusText: 'Unauthorized' });
    expect(fixture.componentInstance['error']()).toBe('Email ou mot de passe incorrect');
    expect(navigate).not.toHaveBeenCalled();
  });
});
