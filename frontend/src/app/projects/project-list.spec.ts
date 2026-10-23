import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ProjectList } from './project-list';

describe('ProjectList', () => {
  let fixture: ComponentFixture<ProjectList>;
  let http: HttpTestingController;

  beforeEach(async () => {
    localStorage.clear();
    localStorage.setItem(
      'pmt_user',
      JSON.stringify({ id: 2, username: 'vincent', email: 'vincent@test.com' }),
    );
    await TestBed.configureTestingModule({
      imports: [ProjectList],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(ProjectList);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('affiche les projets reçus', async () => {
    fixture.detectChanges();
    http
      .expectOne((r) => r.url === '/api/project')
      .flush([
        {
          id: 1,
          title: 'Mon projet',
          description: 'Une description',
          startDate: '2026-10-05',
          creator: { id: 2, username: 'vincent', email: 'vincent@test.com' },
        },
      ]);
    await fixture.whenStable();
    expect(fixture.nativeElement.textContent).toContain('Mon projet');
  });
});
