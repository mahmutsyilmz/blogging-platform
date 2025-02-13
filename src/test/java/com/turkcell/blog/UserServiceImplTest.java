package com.turkcell.blog;

import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.request.UpdateUserDtoRequest;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.dto.response.DashboardDto;
import com.turkcell.blog.dto.response.UserDtoResponse;
import com.turkcell.blog.entity.Role;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.InvalidPasswordException;
import com.turkcell.blog.exception.InvalidVerificationCodeException;
import com.turkcell.blog.exception.UsernameAlreadyExistsException;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.repository.LikeRepository;
import com.turkcell.blog.repository.PostRepository;
import com.turkcell.blog.repository.RoleRepository;
import com.turkcell.blog.repository.UserRepository;
import com.turkcell.blog.security.JwtServiceImpl;
import com.turkcell.blog.service.EmailService;
import com.turkcell.blog.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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

    @Mock
    private EmailService emailService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;



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


    @Test
    public void testSaveVerificationCode_Success() {
        UUID userUuid = UUID.randomUUID();
        String code = "123456";
        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));

        userServiceImpl.saveVerificationCode(userUuid, code);

        assertEquals(code, user.getVerificationCode());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void testVerifyUserEmail_Success() {

        UUID userUuid = UUID.randomUUID();
        String code = "654321";
        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .verificationCode(code)
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));

        boolean result = userServiceImpl.verifyUserEmail(userUuid, code);

        assertTrue(result);
        assertTrue(user.isEmailVerified());
        assertNull(user.getVerificationCode());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testVerifyUserEmail_InvalidCode() {

        UUID userUuid = UUID.randomUUID();
        String code = "111111";
        User user = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("test@example.com")
                .verificationCode("222222")
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));

        assertThrows(InvalidVerificationCodeException.class, () -> userServiceImpl.verifyUserEmail(userUuid, code));
    }


    @Test
    public void testSendRequestNotification_SendsEmailIfVerified() {

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        user.setEmailVerified(true);

        userServiceImpl.sendRequestNotification(user, "CREATE", true);
        verify(emailService, times(1)).sendRequestNotification(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    public void testSendRequestNotification_DoesNotSendIfNotVerified() {
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        user.setEmailVerified(false);
        userServiceImpl.sendRequestNotification(user, "CREATE", true);
        verify(emailService, never()).sendRequestNotification(anyString(), anyString(), anyString());
    }


    @Test
    public void testGetAllUsers() {
        Role role = Role.builder().name("USER").build();

        User user1 = User.builder().uuid(UUID.randomUUID()).username("user1").role(role).build();
        User user2 = User.builder().uuid(UUID.randomUUID()).username("user2").role(role).build();
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        var usersDto = userServiceImpl.getAllUsers();

        assertNotNull(usersDto);
        assertEquals(2, usersDto.size());
    }


    @Test
    public void testGetCurrentUser_Success() {
        String username = "testuser";
        User user = User.builder().uuid(UUID.randomUUID()).username(username).build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        User result = userServiceImpl.getCurrentUser(authentication);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void testGetCurrentUser_UserNotFound() {

        String username = "nonexistent";
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.getCurrentUser(authentication));
    }

    @Test
    public void testDeleteUser_Success() {
        Role role = Role.builder().name("USER").build();

        UUID userUuid = UUID.randomUUID();
        User user = User.builder().uuid(userUuid).username("testuser").role(role).build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));

        UserDtoResponse response = userServiceImpl.deleteUser(userUuid);

        assertNotNull(response);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testGetDashboard() {

        when(userRepository.count()).thenReturn(10L);
        when(postRepository.count()).thenReturn(20L);
        when(likeRepository.count()).thenReturn(30L);

        DashboardDto dashboard = userServiceImpl.getDashboard();

        assertNotNull(dashboard);
        assertEquals(10L, dashboard.getUserCount());
        assertEquals(20L, dashboard.getPostCount());
        assertEquals(30L, dashboard.getLikeCount());
    }


    @Test
    public void testUpdateUser_EmailChanged_SetsEmailVerifiedFalse() {

        UUID userUuid = UUID.randomUUID();
        UpdateUserDtoRequest request = new UpdateUserDtoRequest();
        request.setEmail("newemail@example.com");
        request.setFirstName("NewFirst");
        request.setLastName("NewLast");
        request.setBio("New bio");

        User existingUser = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("oldemail@example.com")
                .firstName("OldFirst")
                .lastName("OldLast")
                .bio("Old bio")
                .emailVerified(true)
                .role(Role.builder().name("USER").build())
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);


        UserDtoResponse response = userServiceImpl.updateUser(userUuid, request);


        assertNotNull(response);
        assertEquals("newemail@example.com", response.getEmail());
        assertFalse(response.isEmailVerified());  // Email verification should be reset
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testUpdateUser_EmailAlreadyExists_ThrowsException() {

        UUID userUuid = UUID.randomUUID();
        UpdateUserDtoRequest request = new UpdateUserDtoRequest();
        request.setEmail("duplicate@example.com");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setBio("Bio");

        User existingUser = User.builder()
                .uuid(userUuid)
                .username("testuser")
                .email("oldemail@example.com")
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userServiceImpl.updateUser(userUuid, request));
    }


    @Test
    public void testGetUserProfile_Success() {

        Role role = Role.builder().name("USER").build();

        UUID userUuid = UUID.randomUUID();
        User user = User.builder().uuid(userUuid).username("testuser").email("test@example.com").role(role).build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));

        UserDtoResponse response = userServiceImpl.getUserProfile(userUuid);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
    }


    @Test
    public void testGetUserByUsername_Success() {

        String username = "testuser";
        User user = User.builder().uuid(UUID.randomUUID()).username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userServiceImpl.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void testGetUserByUsername_UserNotFound() {

        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.getUserByUsername(username));
    }


}


