import { Component, inject, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../core/auth.service";

@Component({
    selector: 'app-login',
    imports: [FormsModule, RouterLink],
    templateUrl: './login.html'
})

export class Login {
    private  readonly auth = inject(AuthService);
    private readonly router = inject(Router);

    protected email = '';
    protected password = '';
    protected readonly error = signal('');

    protected submit(): void {
        this.auth.login({email: this.email, password: this.password}).subscribe( {
            next: () => this.router.navigate(['/projects']),
            error: () => this.error.set('Email ou mot de passe incorrect')
        });
    }
}