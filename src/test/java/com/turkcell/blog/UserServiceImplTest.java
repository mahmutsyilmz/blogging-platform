package com.turkcell.blog;

import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.entity.Role;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.InvalidPasswordException;
import com.turkcell.blog.exception.UsernameAlreadyExistsException;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.repository.RoleRepository;
import com.turkcell.blog.repository.UserRepository;
import com.turkcell.blog.security.JwtServiceImpl;
import com.turkcell.blog.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServiceImpl jwtServiceImpl;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    public void testRegister_Success() {

        RegisterDtoRequest request = new RegisterDtoRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("Password123.");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .role(userRole)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtServiceImpl.generateToken(any(), anyString())).thenReturn("token");

        AuthenticationDtoResponse response = userServiceImpl.register(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertTrue(response.getMessage().contains("successfully registered"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_UsernameAlreadyExists() {

        RegisterDtoRequest request = new RegisterDtoRequest();
        request.setUsername("existinguser");
        request.setEmail("new@example.com");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setPassword("Password123!");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userServiceImpl.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testAuthenticate_Success() {
        // Arrange: Doğru giriş bilgileri
        LoginDtoRequest request = new LoginDtoRequest();
        request.setUsername("testuser");
        request.setPassword("Password123!");

        Role userRole = new Role();
        userRole.setName("USER");

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .password("encodedPassword")
                .role(userRole)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtServiceImpl.generateToken(any(), anyString())).thenReturn("token");

        AuthenticationDtoResponse response = userServiceImpl.authenticate(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertTrue(response.getMessage().contains("successfully authenticated"));
    }

    @Test
    public void testAuthenticate_InvalidPassword() {

        LoginDtoRequest loginRequest = new LoginDtoRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(InvalidPasswordException.class, () -> userServiceImpl.authenticate(loginRequest));
    }

    @Test
    public void testAuthenticate_UserNotFound() {

        LoginDtoRequest loginRequest = new LoginDtoRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("Password123!");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.authenticate(loginRequest));
    }


}


