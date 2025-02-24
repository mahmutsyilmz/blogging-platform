package com.web.blog.service.impl;


import com.web.blog.dto.request.LoginDtoRequest;
import com.web.blog.dto.request.RegisterDtoRequest;
import com.web.blog.dto.request.UpdateUserDtoRequest;
import com.web.blog.dto.response.AuthenticationDtoResponse;
import com.web.blog.dto.response.DashboardDto;
import com.web.blog.dto.response.UserDtoResponse;
import com.web.blog.entity.User;
import com.web.blog.exception.InvalidPasswordException;
import com.web.blog.exception.InvalidVerificationCodeException;
import com.web.blog.exception.UsernameAlreadyExistsException;
import com.web.blog.exception.UsernameNotFoundException;
import com.web.blog.exception.handler.ErrorMessage;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.LikeRepository;
import com.web.blog.repository.PostRepository;
import com.web.blog.repository.RoleRepository;
import com.web.blog.repository.UserRepository;
import com.web.blog.security.JwtServiceImpl;
import com.web.blog.service.EmailService;
import com.web.blog.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final LikeRepository likeRepository;
    private final UserActionLogService userActionLogService;

    public AuthenticationDtoResponse register(RegisterDtoRequest request) {

        // Kullanıcı adı daha önce alınmış mı kontrol et.
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameAlreadyExistsException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS));
        }

        // Yeni kullanıcı oluştur.
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleRepository.findByName("USER").orElseThrow()) // Varsayılan USER veriyoruz
                .build();

        userRepository.save(user);
        userActionLogService.logAction(user.getUsername(), "User registered");


        // Kullanıcı kaydolunca JWT token üretip dönebiliriz.
        String jwtToken = jwtServiceImpl.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().getName())
                        .build(),
                user.getUuid().toString() // user.getUuid().toString() EKLENDİ
        );


        return AuthenticationDtoResponse.builder()
                .token(jwtToken)
                .message(user.getUsername() + " successfully registered.")
                .build();
    }

    public AuthenticationDtoResponse authenticate(LoginDtoRequest request) {
        // Kullanıcıdan gelen username ve password'ü doğrulamak için
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Şifre yanlışsa veya kullanıcı yoksa buraya düşer
            // Önce kullanıcının var olup olmadığını kontrol et
            boolean userExists = userRepository.existsByUsername(request.getUsername());
            if (userExists) {
                // Kullanıcı var ama şifre yanlış
                throw new InvalidPasswordException(
                        new ErrorMessage(MessageType.INVALID_PASSWORD)
                );
            } else {
                // Kullanıcı yok
                throw new UsernameNotFoundException(
                        new ErrorMessage(MessageType.USER_NOT_FOUND)
                );
            }
        }


        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String role = "";
        if (user.getRole().getName().equals("ADMIN")) {
            role = "ADMIN";
        }
        if (user.getRole().getName().equals("USER")) {
            role = "USER";
        }

        // Kullanıcı doğrulandığına göre token üretip dönebiliriz.
        String jwtToken = jwtServiceImpl.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities("ROLE_" + role)
                        .build(),
                user.getUuid().toString()
        );
        userActionLogService.logAction(user.getUsername(), "User logged in");
        return AuthenticationDtoResponse.builder()
                .token(jwtToken)
                .message(user.getRole().getName() + " " + user.getUsername() + " successfully authenticated.")
                .build();
    }

    public void saveVerificationCode(UUID userUuid, String code) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
        user.setVerificationCode(code);
        userRepository.save(user);
        userActionLogService.logAction(user.getUsername(), "Verification code saved");
    }

    public boolean verifyUserEmail(UUID userUuid, String code) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setEmailVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            userActionLogService.logAction(user.getUsername(), "User email verified");
            return true;
        }else throw new InvalidVerificationCodeException(new ErrorMessage(MessageType.INVALID_VERIFICATION_CODE));
    }

    public void sendRequestNotification(User user, String requestType, boolean approved) {
        // Sadece e-posta doğrulanmış kullanıcıya bildirim gönderiyoruz
        if (user.isEmailVerified()) {
            String subject;
            String message;
            if (approved) {
                subject = "Your post request has been approved";
                message = "Dear " + user.getUsername() + ",\n\nYour request for " + requestType +
                        " has been approved.\n\nThank you.";
            } else {
                subject = "Your post request has been rejected";
                message = "Dear " + user.getUsername() + ",\n\nYour request for " + requestType +
                        " has been rejected.\n\nThank you.";
            }
            emailService.sendRequestNotification(user.getEmail(), subject, message);
            userActionLogService.logAction(user.getUsername(), "Request notification sent");
        }
    }


    public List<UserDtoResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserDtoResponse)
                .toList();
    }

    public User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
    }

    public UserDtoResponse deleteUser(UUID userUuid){
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
        userRepository.delete(user);
        return convertToUserDtoResponse(user);
    }

    public DashboardDto getDashboard(){
        return DashboardDto.builder()
                .userCount(userRepository.count())
                .postCount(postRepository.count())
                .likeCount(likeRepository.count())
                .build();
    }

    public UserDtoResponse updateUser(UUID userUuid, UpdateUserDtoRequest request){
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));

        if (!user.getEmail().equals(request.getEmail())) {
            user.setEmailVerified(false);
        }

        if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
            throw new UsernameAlreadyExistsException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS));
        }
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        userRepository.save(user);
        userActionLogService.logAction(user.getUsername(), "User updated");
        return convertToUserDtoResponse(user);
    }

    public UserDtoResponse getUserProfile(UUID userUuid){
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
        userActionLogService.logAction(user.getUsername(), "User profile fetched");
        return convertToUserDtoResponse(user);
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
    }

    private Integer getPostCountByUser(User user) {
        return postRepository.countByUser(user);
    }

    private Integer getLikeCountByUser(User user) {
        return likeRepository.countByUser(user);
    }

    private UserDtoResponse convertToUserDtoResponse(User user) {
        String createdDateStr = (user.getCreatedAt() != null) ? user.getCreatedAt().toString() : "N/A";
        return UserDtoResponse.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .createdDate(createdDateStr)
                .lastName(user.getLastName())
                .role(user.getRole().getName())
                .bio(user.getBio())
                .emailVerified(user.isEmailVerified())
                .postCount(getPostCountByUser(user))
                .likeCount(getLikeCountByUser(user))
                .build();
    }
}