import { Routes } from '@angular/router';
import { Login } from './auth/login';
import { Register } from './auth/register';
import { authGuard } from './core/auth.guard';
import { ProjectList } from './projects/project-list';
import { ProjectCreate } from './projects/project-create';
import { ProjectDetail } from './projects/project-detail';
import { TaskBoard } from './tasks/task-board';
import { TaskCreate } from './tasks/task-create';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'projects', component: ProjectList, canActivate: [authGuard] },
  { path: 'projects/new', component: ProjectCreate, canActivate: [authGuard] },
  { path: 'projects/:id', component: ProjectDetail, canActivate: [authGuard] },
  { path: 'projects/:id/tasks', component: TaskBoard, canActivate: [authGuard] },
  { path: 'projects/:id/tasks/new', component: TaskCreate, canActivate: [authGuard] },
  { path: '', redirectTo: 'projects', pathMatch: 'full' },
];
