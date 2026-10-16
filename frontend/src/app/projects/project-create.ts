import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ProjectService } from './project.service';

@Component({
  selector: 'app-project-create',
  imports: [FormsModule, RouterLink],
  templateUrl: './project-create.html',
})
export class ProjectCreate {
  private readonly projectService = inject(ProjectService);
  private readonly router = inject(Router);

  protected title = '';
  protected description = '';
  protected startDate = '';
  protected readonly error = signal('');

  protected submit(): void {
    this.projectService
      .create({ title: this.title, description: this.description, startDate: this.startDate })
      .subscribe({
        next: (project) => this.router.navigate(['/projects', project.id]),
        error: () => this.error.set('Données invalides'),
      });
  }
}
