import { Component, OnInit, inject, input, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProjectMember, Priority } from '../core/models';
import { ProjectService } from '../projects/project.service';
import { TaskService } from './task.service';

@Component({
  selector: 'app-task-create',
  imports: [FormsModule, RouterLink],
  templateUrl: './task-create.html',
})
export class TaskCreate implements OnInit {
  readonly id = input.required<string>();

  private readonly taskService = inject(TaskService);
  private readonly projectService = inject(ProjectService);
  private readonly router = inject(Router);
  protected readonly members = signal<ProjectMember[]>([]);
  protected readonly error = signal('');
  protected readonly priorities: Priority[] = ['BASSE', 'MOYENNE', 'HAUTE'];
  protected name = '';
  protected description = '';
  protected priority: Priority = 'MOYENNE';
  protected dueDate = '';
  protected assigneeId: number | null = null;

  ngOnInit(): void {
    this.projectService.members(Number(this.id())).subscribe({
      next: (members) => this.members.set(members),
      error: () => this.error.set('Impossible de charger les membres'),
    });
  }

  protected submit(): void {
    this.taskService
      .create(Number(this.id()), {
        name: this.name,
        description: this.description,
        priority: this.priority,
        dueDate: this.dueDate,
        assigneeId: this.assigneeId,
      })
      .subscribe({
        next: () => this.router.navigate(['/projects', this.id(), 'tasks']),
        error: () => this.error.set('Création impossible'),
      });
  }
}
