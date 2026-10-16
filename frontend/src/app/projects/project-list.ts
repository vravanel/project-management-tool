import { DatePipe } from "@angular/common";
import { Component, OnInit, signal, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Project } from "../core/models";
import { ProjectService } from "./project.service";

@Component({
    selector: 'app-project-list',
    imports: [RouterLink, DatePipe],
    templateUrl: './project-list.html'
})

export class ProjectList implements OnInit {
    private readonly projectService = inject(ProjectService);
    protected readonly projects = signal<Project[]>([]);
    protected readonly error = signal('');

    ngOnInit(): void {
        this.projectService.list().subscribe({
            next: (projects) => this.projects.set(projects),
            error: () => this.error.set('Impossible de charger les projets')
        });
    }
}