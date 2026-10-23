import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ProjectService } from './project.service';

describe('ProjectService', () => {
  let service: ProjectService;
  let http: HttpTestingController;

  function setup(connected = true): void {
    localStorage.clear();
    if (connected) {
      localStorage.setItem(
        'pmt_user',
        JSON.stringify({ id: 2, username: 'vince', email: 'vince@test.com' }),
      );
    }
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ProjectService);
    http = TestBed.inject(HttpTestingController);
  }

  afterEach(() => http.verify());

  it("liste les projets de l'utilisateur connecté", () => {
    setup();
    service.list().subscribe();
    const call = http.expectOne((r) => r.url === '/api/project');
    expect(call.request.method).toBe('GET');
    expect(call.request.params.get('userId')).toBe('2');
    call.flush([]);
  });

  it("crée un projet avec l'id de l'utilisateur dans l'URL", () => {
    setup();
    const request = { title: 'Projet', description: 'Une description', startDate: '2026-10-05' };
    service.create(request).subscribe();
    const call = http.expectOne('/api/project/2');
    expect(call.request.method).toBe('POST');
    expect(call.request.body).toEqual(request);
    call.flush({});
  });

  it("modifie le rôle d'un membre", () => {
    setup();
    service.updateRole(3, 12, 'OBSERVATEUR').subscribe();
    const call = http.expectOne((r) => r.url === '/api/project/3/members/12');
    expect(call.request.method).toBe('PUT');
    expect(call.request.body).toEqual({ role: 'OBSERVATEUR' });
  });

  it("refuse d'appeler l'api quand la personne n'est pas connecté", () => {
    setup(false);
    expect(() => service.list()).toThrow('Utilisateur non connecté');
  });
});
