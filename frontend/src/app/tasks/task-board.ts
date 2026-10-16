import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { ProjectMember, Status, Task } from '../core/models';
import { ProjectService } from '../projects/project.service';
import { TaskService } from './task.service';

interface Column {
  status: Status;
  label: string;
}

@Component({
  selector: 'app-task-board',
  imports: [RouterLink, DatePipe],
  templateUrl: './task-board.html'
})
export class TaskBoard implements OnInit {
  readonly id = input.required<string>();

  private readonly taskService = inject(TaskService);
  private readonly projectService = inject(ProjectService);
  private readonly auth = inject(AuthService);
  protected readonly tasks = signal<Task[]>([]);
  protected readonly members = signal<ProjectMember[]>([]);
  protected readonly error = signal('');
  protected readonly columns: Column[] = [
    { status: 'A_FAIRE', label: 'A faire' },
    { status: 'EN_COURS', label: 'En cours' },
    { status: 'FAIT', label: 'Fait' },
  ];
  protected readonly canEdit = computed(() =>
    this.members().some((m) => m.user.id === this.auth.user()?.id && m.role !== 'OBSERVATEUR'),
  );

  ngOnInit(): void {
    const projectId = Number(this.id());
    this.taskService.list(projectId).subscribe({
      next: (tasks) => this.tasks.set(tasks),
      error: () => this.error.set('Impossible de charger les tâches'),
    });
    this.projectService.members(projectId).subscribe({
      next: (members) => this.members.set(members),
      error: () => this.error.set('Impossible de charger les membres'),
    });
  }

  protected tasksOf(status: Status): Task[] {
    return this.tasks().filter((task) => task.status === status);
  }
}
