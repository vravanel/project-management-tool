import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppNotification } from '../core/models';
import { NotificationList } from './notification-list';

describe('NotificationList', () => {
  const notifications: AppNotification[] = [
    {
      id: 1,
      taskId: 10,
      taskName: 'Ancienne',
      type: 'TASK_ASSIGNED',
      sentAt: '2026-10-18T09:00:00',
      isRead: true,
    },
    {
      id: 2,
      taskId: 11,
      taskName: 'Récente',
      type: 'TASK_ASSIGNED',
      sentAt: '2026-10-18T12:00:00',
      isRead: false,
    },
  ];
  let fixture: ComponentFixture<NotificationList>;
  let http: HttpTestingController;

  beforeEach(async () => {
    localStorage.clear();
    localStorage.setItem(
      'pmt_user',
      JSON.stringify({ id: 1, username: 'vincent', email: 'vincent@test.com' }),
    );
    await TestBed.configureTestingModule({
      imports: [NotificationList],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationList);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('affiche les notifications de la plus récente à la plus ancienne', async () => {
    http.expectOne('/api/users/1/notifications').flush(notifications);
    await fixture.whenStable();

    const sorted = fixture.componentInstance['sorted']();
    expect(sorted.map((n: AppNotification) => n.id)).toEqual([2, 1]);
    expect(fixture.nativeElement.textContent).toContain('Récente');
  });
});
