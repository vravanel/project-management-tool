import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { Register } from './register';

describe('Register', () => {
  let fixture: ComponentFixture<Register>;
  let http: HttpTestingController;
  let navigate: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(Register);
    http = TestBed.inject(HttpTestingController);
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
  });

  afterEach(() => http.verify());

  it('redirige vers la connexion après une inscription réussie', () => {
    fixture.componentInstance['username'] = 'vincent';
    fixture.componentInstance['email'] = 'vincent@test.com';
    fixture.componentInstance['password'] = 'motdepasse123';
    fixture.componentInstance['submit']();
    const call = http.expectOne('/api/auth/register');
    expect(call.request.body).toEqual({
      username: 'vincent',
      email: 'vincent@test.com',
      password: 'motdepasse123',
    });
    call.flush({ id: 1, username: 'vincent', email: 'vincent@test.com' });
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });

  it("affiche un message quand l'email est déjà utilisé", () => {
    fixture.componentInstance['submit']();
    http.expectOne('/api/auth/register').flush('conflit', { status: 409, statusText: 'Conflict' });
    expect(fixture.componentInstance['error']()).toBe('Cet email est déjà utilisé');
  });
});
