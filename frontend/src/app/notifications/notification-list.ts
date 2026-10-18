import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AppNotification } from '../core/models';
import { NotificationService } from './notification.service';

@Component({
  selector: 'app-notification-list',
  imports: [RouterLink, DatePipe],
  templateUrl: './notification-list.html',
})
export class NotificationList implements OnInit {
  private readonly notificationService = inject(NotificationService);
  protected readonly error = signal('');
  protected readonly sorted = computed(() =>
    [...this.notificationService.notifications()].sort((a, b) => b.sentAt.localeCompare(a.sentAt)),
  );

  ngOnInit(): void {
    this.notificationService.load();
  }

  protected markAsRead(notification: AppNotification): void {
    this.notificationService.markAsRead(notification.id).subscribe({
      error: () => this.error.set('Impossible de marquer la notification comme lue'),
    });
  }

  protected describe(notification: AppNotification): string {
    return notification.type === 'TASK_ASSIGNED'
      ? 'Une tâche vous a été assignée'
      : notification.type;
  }
}
