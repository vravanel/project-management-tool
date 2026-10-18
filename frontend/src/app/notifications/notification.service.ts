import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthService } from '../core/auth.service';
import { AppNotification } from '../core/models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);
  readonly notifications = signal<AppNotification[]>([]);
  readonly unreadCount = computed(() => this.notifications().filter((n) => !n.isRead).length);

  load(): void {
    const user = this.auth.user();
    if (!user) {
      this.notifications.set([]);
      return;
    }
    this.http.get<AppNotification[]>(`/api/users/${user.id}/notifications`).subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: () => this.notifications.set([]),
    });
  }

  markAsRead(id: number): Observable<AppNotification> {
    const user = this.auth.user();
    if (!user) {
      throw new Error('Utilisateur non connecté');
    }
    return this.http
      .put<AppNotification>(`/api/notifications/${id}/read`, null, {
        params: { userId: user.id },
      })
      .pipe(tap(() => this.load()));
  }
}
