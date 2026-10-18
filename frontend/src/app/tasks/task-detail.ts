import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { Priority, ProjectMember, Status, Task, TaskHistory } from '../core/models';
import { ProjectService } from '../projects/project.service';
import { TaskService } from './task.service';

@Component({
  selector: 'app-task-detail',
  imports: [FormsModule, RouterLink, DatePipe],
  templateUrl: './task-detail.html',
})
export class TaskDetail implements OnInit {
  readonly id = input.required<string>();

  private readonly taskService = inject(TaskService);
  private readonly projectService = inject(ProjectService);
  private readonly auth = inject(AuthService);
  protected readonly task = signal<Task | null>(null);
  protected readonly members = signal<ProjectMember[]>([]);
  protected readonly history = signal<TaskHistory[]>([]);
  protected readonly error = signal('');
  protected readonly message = signal('');
  protected readonly priorities: Priority[] = ['BASSE', 'MOYENNE', 'HAUTE'];
  protected readonly statuses: Status[] = ['A_FAIRE', 'EN_COURS', 'FAIT'];
  protected readonly canEdit = computed(() =>
    this.members().some((m) => m.user.id === this.auth.user()?.id && m.role !== 'OBSERVATEUR'),
  );
  protected name = '';
  protected description = '';
  protected priority: Priority = 'MOYENNE';
  protected status: Status = 'A_FAIRE';
  protected dueDate = '';
  protected endDate = '';
  protected assigneeId: number | null = null;

  private readonly fieldLabels: Record<string, string> = {
    name: 'Nom',
    description: 'Description',
    priority: 'Priorité',
    status: 'Statut',
    dueDate: 'Echéance',
    endDate: 'Date de fin',
    assignee: 'Assignée à',
  };

  ngOnInit(): void {
    this.taskService.get(this.taskId()).subscribe({
      next: (task) => {
        this.applyTask(task);
        this.loadMembers(task.projectId);
        this.loadHistory();
      },
      error: () => this.error.set('Tâche introuvable ou accès refusé'),
    });
  }

  protected save(): void {
    this.taskService
      .update(this.taskId(), {
        name: this.name,
        description: this.description,
        priority: this.priority,
        status: this.status,
        dueDate: this.dueDate,
        endDate: this.endDate || null,
        assigneeId: this.assigneeId,
      })
      .subscribe({
        next: (task) => {
          this.applyTask(task);
          this.error.set('');
          this.message.set('Tâche enregistrée');
          this.loadHistory();
        },
        error: (e: HttpErrorResponse) => {
          this.message.set('');
          this.error.set(
            e.status === 403 ? 'Vous ne pouvez pas modifier cette tâche' : 'Données invalides',
          );
        },
      });
  }

  protected label(field: string): string {
    return this.fieldLabels[field] ?? field;
  }

  private taskId(): number {
    return Number(this.id());
  }

  private applyTask(task: Task): void {
    this.task.set(task);
    this.name = task.name;
    this.description = task.description;
    this.priority = task.priority;
    this.status = task.status;
    this.dueDate = task.dueDate;
    this.endDate = task.endDate ?? '';
    this.assigneeId = task.assignee?.id ?? null;
  }

  private loadMembers(projectId: number): void {
    this.projectService.members(projectId).subscribe({
      next: (members) => this.members.set(members),
      error: () => this.error.set('Impossible de charger les membres'),
    });
  }

  private loadHistory(): void {
    this.taskService.history(this.taskId()).subscribe({
      next: (history) => this.history.set(history),
      error: () => this.error.set('Impossible de charger historique'),
    });
  }
}
