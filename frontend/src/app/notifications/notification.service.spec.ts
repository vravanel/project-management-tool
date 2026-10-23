import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AppNotification } from '../core/models';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;
  let http: HttpTestingController;

  const notifications: AppNotification[] = [
    {id: 1, taskId:10, taskName: 'Tâche A', type: 'TASK_ASSIGNED', sentAt: '2026-10-18T10:00:00', isRead: false},
    {id: 2, taskId:11, taskName: 'Tâche B', type: 'TASK_ASSIGNED', sentAt: '2026-10-18T11:00:00', isRead: true}
  ]

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
    service = TestBed.inject(NotificationService);
    http = TestBed.inject(HttpTestingController);
  }

  afterEach(() => http.verify());

  it('charge les notifications et compte les non lues', () => {
    setup();
    service.load();
    http.expectOne('/api/users/2/notifications').flush(notifications);
    expect(service.notifications()).toHaveLength(2);
    expect(service.unreadCount()).toBe(1);
  })

  it('vide la liste quand persone est connecté', () => {
    setup(false);
    service.load();
    expect(service.notifications()).toEqual([]);
    expect(service.unreadCount()).toBe(0);
  })

  it('marque une notification comme lue puis recharge la liste', () => {
    setup();
    service.markAsRead(1).subscribe();
    const put = http.expectOne((r) => r.url === '/api/notifications/1/read');
    expect(put.request.method).toBe('PUT');
    expect(put.request.params.get('userId')).toBe('2');
    put.flush({ ...notifications[0], isRead: true});
    http.expectOne('/api/users/2/notifications').flush([{...notifications[0], isRead: true}, notifications[1]]);
    expect(service.unreadCount()).toBe(0);
  });
});