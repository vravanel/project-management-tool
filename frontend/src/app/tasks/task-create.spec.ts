import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { TaskCreate } from './task-create';

describe('TaskCreate', () => {
  const vincent = { id: 1, username: 'vincent', email: 'vincent@test.com' };
  let fixture: ComponentFixture<TaskCreate>;
  let http: HttpTestingController;
  let navigate: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    localStorage.clear();
    localStorage.setItem('pmt_user', JSON.stringify(vincent));
    await TestBed.configureTestingModule({
      imports: [TaskCreate],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(TaskCreate);
    http = TestBed.inject(HttpTestingController);
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fixture.componentRef.setInput('id', '3');
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('crée la tâche puis retourne au tableau de board', async () => {
    http.expectOne((r) => r.url === '/api/project/3/members').flush([]);
    fixture.componentInstance['name'] = 'Ma tâche';
    fixture.componentInstance['description'] = 'Une description';
    fixture.componentInstance['dueDate'] = '2026-11-01';
    fixture.componentInstance['assigneeId'] = 7;
    fixture.componentInstance['submit']();

    const call = http.expectOne((r) => r.url === '/api/project/3/tasks');
    expect(call.request.body).toEqual({
      name: 'Ma tâche',
      description: 'Une description',
      priority: 'MOYENNE',
      dueDate: '2026-11-01',
      assigneeId: 7,
    });

    call.flush({});
    expect(navigate).toHaveBeenCalledWith(['/projects', '3', 'tasks']);
  });
});
