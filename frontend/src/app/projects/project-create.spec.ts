import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { ProjectCreate } from './project-create';

describe('ProjectCreate', () => {
  let fixture: ComponentFixture<ProjectCreate>;
  let http: HttpTestingController;
  let navigate: ReturnType<typeof vi.spyOn>;

  beforeEach(async () => {
    localStorage.clear();
    localStorage.setItem(
      'pmt_user',
      JSON.stringify({ id: 2, username: 'vincent', email: 'vincent@test.com' }),
    );
    await TestBed.configureTestingModule({
      imports: [ProjectCreate],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(ProjectCreate);
    http = TestBed.inject(HttpTestingController);
    navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
  });

  afterEach(() => http.verify());

  it('crée le projet puis ouvre son détail', () => {
    fixture.componentInstance['title'] = 'Mon projet';
    fixture.componentInstance['description'] = 'Une description';
    fixture.componentInstance['startDate'] = '2026-10-05';
    fixture.componentInstance['submit']();
    const call = http.expectOne((r) => r.url === '/api/project/2');
    expect(call.request.body).toEqual({
      title: 'Mon projet',
      description: 'Une description',
      startDate: '2026-10-05',
    });
    call.flush({ id: 4 });
    expect(navigate).toHaveBeenCalledWith(['/projects', 4]);
  });
});
