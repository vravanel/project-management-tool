import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../core/auth.service';
import {
  MemberInviteRequest,
  Project,
  ProjectCreateRequest,
  ProjectMember,
  Role,
} from '../core/models';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  list(): Observable<Project[]> {
    return this.http.get<Project[]>('/api/project', { params: { userId: this.userId() } });
  }

  get(id: number): Observable<Project> {
    return this.http.get<Project>(`/api/project/${id}`, { params: { userId: this.userId() } });
  }

  create(request: ProjectCreateRequest): Observable<Project> {
    return this.http.post<Project>(`/api/project/${this.userId()}`, request);
  }

  members(projectId: number): Observable<ProjectMember[]> {
    return this.http.get<ProjectMember[]>(`/api/project/${projectId}/members`, {
      params: { userId: this.userId() },
    });
  }

  invite(projectId: number, request: MemberInviteRequest): Observable<ProjectMember> {
    return this.http.post<ProjectMember>(`/api/project/${projectId}/members`, request, {
      params: { userId: this.userId() },
    });
  }

  updateRole(projectId: number, memberId: number, role: Role): Observable<ProjectMember> {
    return this.http.put<ProjectMember>(
      `/api/project/${projectId}/members/${memberId}`,
      { role },
      { params: { userId: this.userId() } },
    );
  }

  private userId(): number {
    const user = this.auth.user();
    if (!user) {
      throw new Error('Utilisateur non connecté');
    }
    return user.id;
  }
}
