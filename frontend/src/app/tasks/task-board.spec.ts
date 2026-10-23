import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TaskBoard } from './task-board';
import { Role, Status, Task } from '../core/models';

describe('TaskBoard', () => {
  const vincent = { id: 1, username: 'vincent', email: 'vincent@test.com' };
  let fixture: ComponentFixture<TaskBoard>;
  let http: HttpTestingController;

  function task(id: number, status: Status): Task {
    return {
      id,
      name: `Tâche ${id}`,
      description: 'Une description',
      priority: 'MOYENNE',
      status,
      dueDate: '2026-11-01',
      endDate: null,
      assignee: null,
      projectId: 3,
      createdAt: '2026-10-18T10:00:00',
      updatedAt: null,
    };
  }

  async function open(myRole: Role, tasks: Task[]): Promise<void> {
    fixture.componentRef.setInput('id', 3);
    fixture.detectChanges();
    http.expectOne((r) => r.url === '/api/project/3/tasks').flush(tasks);
    http
      .expectOne((r) => r.url === '/api/project/3/members')
      .flush([{ id: 4, user: vincent, projectId: 3, role: myRole }]);
    await fixture.whenStable();
  }

  beforeEach(async () => {
    localStorage.clear();
    localStorage.setItem('pmt_user', JSON.stringify(vincent));
    await TestBed.configureTestingModule({
      imports: [TaskBoard],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(TaskBoard);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('classe les tâches par colonne de statut', async () => {
    await open('MEMBRE', [
      task(1, 'A_FAIRE'),
      task(2, 'EN_COURS'),
      task(3, 'FAIT'),
      task(4, 'FAIT'),
    ]);
    expect(fixture.componentInstance['tasksOf']('A_FAIRE')).toHaveLength(1);
    expect(fixture.componentInstance['tasksOf']('EN_COURS')).toHaveLength(1);
    expect(fixture.componentInstance['tasksOf']('FAIT')).toHaveLength(2);
    expect(fixture.nativeElement.textContent).toContain('Tâche 4');
  });

  it('interdit la création à un observateur', async () => {
    await open('OBSERVATEUR', []);
    expect(fixture.componentInstance['canEdit']()).toBe(false);
    expect(fixture.nativeElement.textContent).not.toContain('Nouvelle tâche');
  });
});
