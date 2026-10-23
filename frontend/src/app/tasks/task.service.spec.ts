import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
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
    service = TestBed.inject(TaskService);
    http = TestBed.inject(HttpTestingController);
  }

  afterEach(() => http.verify());

  it("liste les tâches d'un projet", () => {
    setup();
    service.list(3).subscribe();
    const call = http.expectOne((r) => r.url === '/api/project/3/tasks');
    expect(call.request.method).toBe('GET');
    expect(call.request.params.get('userId')).toBe('2');
    call.flush([]);
  });

  it('modifie une tâche', () => {
    setup();
    const request = {
      name: 'Tâche',
      description: 'Une description',
      priority: 'MOYENNE' as const,
      status: 'EN_COURS' as const,
      dueDate: '2026-11-01',
      endDate: null,
      assigneeId: 2,
    };

    service.update(10, request).subscribe();

    const call = http.expectOne((r) => r.url === '/api/tasks/10');
    expect(call.request.method).toBe('PUT');
    expect(call.request.body).toEqual(request);
    call.flush({});
  });

  it("refuse d'appeler l'api quand la personne n'est pas connecté", () => {
    setup(false);
    expect(() => service.get(10)).toThrow('Utilisateur non connecté');
  });
});
