import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { Project, ProjectMember, Role } from '../core/models';
import { ProjectService } from './project.service';

@Component({
  selector: 'app-project-detail',
  imports: [FormsModule, RouterLink, DatePipe],
  templateUrl: './project-detail.html'
})
export class ProjectDetail implements OnInit {
  readonly id = input.required<string>();
  private readonly projectService = inject(ProjectService);
  private readonly auth = inject(AuthService);

  protected readonly project = signal<Project | null>(null);
  protected readonly members = signal<ProjectMember[]>([]);
  protected readonly error = signal('');
  protected readonly roles: Role[] = ['ADMINISTRATEUR', 'MEMBRE', 'OBSERVATEUR'];
  protected readonly isAdmin = computed(() =>
    this.members().some((m) => m.user.id === this.auth.user()?.id && m.role === 'ADMINISTRATEUR'),
  );
  protected inviteEmail = '';
  protected inviteRole: Role = 'MEMBRE';

  ngOnInit(): void {
    this.projectService.get(this.projectId()).subscribe({
      next: (project) => this.project.set(project),
      error: () => this.error.set('Projet introuvable ou accès refusé'),
    });
    this.loadMembers();
  }

  protected invite(): void {
    this.projectService
      .invite(this.projectId(), {
        email: this.inviteEmail,
        role: this.inviteRole,
      })
      .subscribe({
        next: () => {
          this.inviteEmail = '';
          this.error.set('');
          this.loadMembers();
        },
        error: (e: HttpErrorResponse) => this.error.set(this.inviteErrorMessage(e)),
      });
  }

  protected changeRole(member: ProjectMember, role: string): void {
    this.projectService.updateRole(this.projectId(), member.id, role as Role).subscribe({
      next: () => {
        this.error.set('');
        this.loadMembers();
      },
      error: () => this.error.set('Impossible de modifier le rôle'),
    });
  }

  private projectId(): number {
    return Number(this.id());
  }

  private loadMembers(): void {
    this.projectService.members(this.projectId()).subscribe({
      next: (members) => this.members.set(members),
      error: () => this.error.set('Impossible de charger les membres'),
    });
  }

  private inviteErrorMessage(e: HttpErrorResponse): string {
    switch (e.status) {
      case 404:
        return 'Aucun utilisateur avec cet email';
      case 409:
        return 'Cet utilisateur est déjà membre du projet';
      case 403:
        return 'Seul un administrateur peut inviter des membres';
      default:
        return "Impossible d'inviter ce membre";
    }
  }
}
