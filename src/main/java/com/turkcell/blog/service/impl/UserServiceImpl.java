package com.turkcell.blog.service.impl;


import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.dto.response.DashboardDto;
import com.turkcell.blog.dto.response.UserDtoResponse;
import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.InvalidPasswordException;
import com.turkcell.blog.exception.UsernameAlreadyExistsException;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.LikeRepository;
import com.turkcell.blog.repository.PostRepository;
import com.turkcell.blog.repository.RoleRepository;
import com.turkcell.blog.repository.UserRepository;
import com.turkcell.blog.security.JwtServiceImpl;
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

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final LikeRepository likeRepository;

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
        return AuthenticationDtoResponse.builder()
                .token(jwtToken)
                .message(user.getRole().getName() + " " + user.getUsername() + " successfully authenticated.")
                .build();
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

    public UserDtoResponse getUserProfile(UUID userUuid){
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
        return convertToUserDtoResponse(user);
    }

    private Integer getPostCountByUser(User user) {
        return postRepository.countByUser(user);
    }

    private Integer getLikeCountByUser(User user) {
        return likeRepository.countByUser(user);
    }

    private UserDtoResponse convertToUserDtoResponse(User user) {
        return UserDtoResponse.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .createdDate(user.getCreatedAt().toString())
                .lastName(user.getLastName())
                .role(user.getRole().getName())
                .bio(user.getBio())
                .postCount(getPostCountByUser(user))
                .likeCount(getLikeCountByUser(user))
                .build();
    }
}