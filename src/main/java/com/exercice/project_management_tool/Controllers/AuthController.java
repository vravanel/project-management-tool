package com.exercice.project_management_tool.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.UserLoginRequest;
import com.exercice.project_management_tool.DTO.UserRegisterRequest;
import com.exercice.project_management_tool.DTO.UserResponse;
import com.exercice.project_management_tool.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Inscrire un nouvel utilisateur", description = "Crée un compte à partir d'un nom utilisateur, un email et un mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte créé"),
        @ApiResponse(responseCode = "400", description = "Donnée invalides"),
        @ApiResponse(responseCode = "409", description = "Un compte existe déjà avec cet email")
    })
    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid UserRegisterRequest request) {
        return this.userService.register(request);
    }

    @Operation(summary = "Se connecter", description = "Vérifie l'email et le mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "400", description = "Champ manquant"),
        @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login")
    public UserResponse login(@RequestBody @Valid UserLoginRequest request) {
        return this.userService.login(request);
    }

}
