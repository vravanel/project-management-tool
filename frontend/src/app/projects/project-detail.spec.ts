import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ProjectDetail } from './project-detail';
import { Role } from '../core/models';

describe('ProjectDetail', () => {
  const vincent = { id: 1, username: 'vincent', email: 'vincent@test.com' };
  const alice = { id: 2, username: 'test', email: 'test@test.com' };
  const project = {
    id: 1,
    title: 'Mon projet',
    description: 'Une description',
    startDate: '2026-10-05',
    creator: vincent,
  };
  let fixture: ComponentFixture<ProjectDetail>;
  let http: HttpTestingController;

  async function open(myRole: Role): Promise<void> {
    fixture.componentRef.setInput('id', 3);
    fixture.detectChanges();
    http.expectOne((r) => r.url === '/api/project/3').flush(project);
    http
      .expectOne((r) => r.url === '/api/project/3/members')
      .flush([
        { id: 4, user: vincent, projectId: 3, role: myRole },
        { id: 2, user: alice, projectId: 3, role: 'MEMBRE' },
      ]);
    await fixture.whenStable();
  }

  beforeEach(async () => {
    localStorage.clear();
    localStorage.setItem('pmt_user', JSON.stringify(vincent));
    await TestBed.configureTestingModule({
      imports: [ProjectDetail],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(ProjectDetail);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('affiche la formulaire invitation à un administrateur uniquement', async () => {
    await open('ADMINISTRATEUR');
    expect(fixture.componentInstance['isAdmin']()).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Inviter un membre');
  });
});
