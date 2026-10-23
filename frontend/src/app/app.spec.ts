import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';

describe('App', () => {
  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('déconnecte et redirige vers la page de connexion', () => {
    localStorage.setItem(
      'pmt_user',
      JSON.stringify({ id: 1, username: 'vincent', email: 'v@test.com' }),
    );
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const fixture = TestBed.createComponent(App);
    fixture.componentInstance['logout']();

    expect(localStorage.getItem('pmt_user')).toBeNull();
    expect(navigate).toHaveBeenCalledWith(['/login']);
    TestBed.inject(HttpTestingController);
  });
});
