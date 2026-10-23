import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
  UrlTree,
  provideRouter,
} from '@angular/router';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  function runGuard(): boolean | UrlTree {
    return TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    ) as boolean | UrlTree;
  }

  beforeEach(() => {
    localStorage.clear();
  });

  function configure(): void {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
  }

  it('laisse passer un utilisateur connecté', () => {
    localStorage.setItem(
      'pmt_user',
      JSON.stringify({ id: 1, username: 'vince', email: 'vince@test.com' }),
    );
    configure();
    expect(runGuard()).toBe(true);
  });

  it('redirige un visiteur vers la page de connexion', () => {
    configure();
    const result = runGuard();
    expect(result).toBeInstanceOf(UrlTree);
    expect(TestBed.inject(Router).serializeUrl(result as UrlTree)).toBe('/login');
  });
});
