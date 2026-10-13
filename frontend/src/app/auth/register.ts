import { Component, inject, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../core/auth.service";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
    selector: 'app-register',
    imports: [FormsModule, RouterLink],
    templateUrl: './register.html'
})

export class Register {
    private  readonly auth = inject(AuthService);
    private readonly router = inject(Router);

    protected username = '';
    protected email = '';
    protected password = '';
    protected readonly error = signal('');

    protected submit(): void {
        this.auth.register({username: this.username, email: this.email, password: this.password}).subscribe( {
            next: () => this.router.navigate(['/login']),
            error: (e: HttpErrorResponse) => this.error.set(e.status === 409 ? 'Cet email est déjà utilisé' : 'Données non valide')
        });
    }
}