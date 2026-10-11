package com.exercice.project_management_tool.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.exercice.project_management_tool.DTO.UserLoginRequest;
import com.exercice.project_management_tool.DTO.UserRegisterRequest;
import com.exercice.project_management_tool.DTO.UserResponse;
import com.exercice.project_management_tool.Entity.User;
import com.exercice.project_management_tool.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks 
    private UserService userService;

    @Test 
    void register_creeLUtilisateurAvecMotDePasseHashe() {
        when(userRepository.findByEmail("vincent@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    
        UserResponse response = userService.register(new UserRegisterRequest("vincent", "vincent@test.com", "motdepasse123"));
        assertThat(response.username()).isEqualTo("vincent");
        assertThat(response.email()).isEqualTo("vincent@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test 
    void register_emailDejaUtiliser_leve409() {
        when(userRepository.findByEmail("vincent@test.com")).thenReturn(Optional.of(new User()));
        assertThatThrownBy(() -> userService.register(new UserRegisterRequest("vincent", "vincent@test.com", "motdepasse123")))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                    .isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any(User.class));            
    }

    @Test 
    void login_identifiantsCorrects_retourneLUtilisateur() {
        User user = new User();
        user.setUsername("vincent");
        user.setEmail("vincent@test.com");
        user.setPassword(new BCryptPasswordEncoder().encode("motdepasse123"));
        when(userRepository.findByEmail("vincent@test.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.login(new UserLoginRequest("vincent@test.com", "motdepasse123"));

        assertThat(response.email()).isEqualTo("vincent@test.com");
    }

    @Test 
    void login_mauvaisMotDePasse_leve401() {
        User user = new User();
        user.setEmail("vincent@test.com");
        user.setPassword(new BCryptPasswordEncoder().encode("motdepasse123"));
        when(userRepository.findByEmail("vincent@test.com")).thenReturn(Optional.of(user));
    
        assertThatThrownBy(() -> userService.login(new UserLoginRequest("vincent@test.com", "motdepasse")))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test 
    void login_emailInconnu_leve401() {
        when(userRepository.findByEmail("vincent1@test.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.login(new UserLoginRequest("vincent1@test.com", "motdepasse")))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
