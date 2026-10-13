export type Role = 'ADMINISTRATEUR' | 'MEMBRE' | 'OBSERVATEUR';
export type Priority = 'BASSE' | 'MOYENNE' | 'HAUTE';
export type Status = 'A_FAIRE' | 'EN_COURS' | 'FAIT';

export interface User {
    id: number;
    username: string;
    email: string;
}

export interface Project {
    id: number;
    title: string;
    description: string;
    startDate: string;
    creator: User;
}

export interface ProjectMember  {
    id: number;
    user: User;
    projectId: number;
    role: Role;
}

export interface Task {
    id: number;
    name: string;
    description: string;
    priority: Priority;
    status: Status;
    dueDate: string;
    endDate: string | null;
    assignee: User | null;
    projectId: number;
    createdAt: string;
    updatedAt: string | null;
}

export interface TaskHistory {
    id: number;
    fieldName: string;
    fieldValueOld: string | null;
    fieldValueNew: string | null;
    modifiedAt: string;
    user: User | null;
}

export interface AppNotification {
    id: number;
    taskId: number;
    taskName: string;
    type: string;
    sendAt: string;
    isRead: boolean;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface ProjectCreateRequest {
    title: string;
    description: string;
    startDate: string;
}

export interface MemberInviteRequest {
    email: string;
    role: Role;
}

export interface TaskCreateRequest {
    name: string;
    description: string;
    priority: Priority;
    dueDate: string;
    assigneeId: number | null;
}

export interface TaskUpdateRequest extends TaskCreateRequest {
    status: Status;
    endDate: string | null;
}