package com.exercice.project_management_tool.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice.project_management_tool.DTO.UserLoginRequest;
import com.exercice.project_management_tool.DTO.UserRegisterRequest;
import com.exercice.project_management_tool.DTO.UserResponse;
import com.exercice.project_management_tool.Service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid UserRegisterRequest request) {
        return this.userService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody @Valid UserLoginRequest request) {
        return this.userService.login(request);
    }

}
