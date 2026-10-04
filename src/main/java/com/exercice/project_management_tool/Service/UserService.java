package com.exercice.project_management_tool.Service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.UserLoginRequest;
import com.exercice.project_management_tool.DTO.UserRegisterRequest;
import com.exercice.project_management_tool.DTO.UserResponse;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(UserRegisterRequest request) {
        this.userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email existe déjà");
        });

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(this.passwordEncoder.encode(request.password()));

        return UserResponse.from(this.userRepository.save(user));
    }

    public UserResponse login(UserLoginRequest request) {
        User user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));
        if (!this.passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        return UserResponse.from(user);
    }
}
