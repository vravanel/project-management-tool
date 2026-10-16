import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../core/auth.service';
import { Task, TaskCreateRequest, TaskHistory, TaskUpdateRequest } from '../core/models';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  list(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`/api/project/${projectId}/tasks`, {
      params: { userId: this.userId() },
    });
  }

  create(projectId: number, request: TaskCreateRequest): Observable<Task> {
    return this.http.post<Task>(`/api/project/${projectId}/tasks`, request, {
      params: { userId: this.userId() },
    });
  }

  get(id: number): Observable<Task> {
    return this.http.get<Task>(`/api/tasks/${id}`, {
      params: {
        userId: this.userId(),
      },
    });
  }

  update(id: number, request: TaskUpdateRequest): Observable<Task> {
    return this.http.put<Task>(`/api/tasks/${id}`, request, {
      params: { userId: this.userId() },
    });
  }

  history(id: number): Observable<TaskHistory[]> {
    return this.http.get<TaskHistory[]>(`/api/tasks/${id}/history`, {
      params: { userId: this.userId() },
    });
  }

  private userId(): number {
    const user = this.auth.user();
    if (!user) {
      throw new Error('Utilisateur non connecté');
    }
    return user.id;
  }
}
