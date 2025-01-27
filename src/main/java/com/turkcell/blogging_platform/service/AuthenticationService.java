package com.turkcell.blogging_platform.service;


import com.turkcell.blogging_platform.dto.request.LoginRequest;
import com.turkcell.blogging_platform.dto.request.RegisterRequest;
import com.turkcell.blogging_platform.dto.response.AuthenticationResponse;
import com.turkcell.blogging_platform.entity.Role;
import com.turkcell.blogging_platform.entity.User;
import com.turkcell.blogging_platform.exception.BaseException;
import com.turkcell.blogging_platform.exception.InvalidPasswordException;
import com.turkcell.blogging_platform.exception.UsernameAlreadyExistsException;
import com.turkcell.blogging_platform.exception.UsernameNotFoundException;
import com.turkcell.blogging_platform.exception.handler.ErrorMessage;
import com.turkcell.blogging_platform.exception.handler.MessageType;
import com.turkcell.blogging_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        // Kullanıcı adı daha önce alınmış mı kontrol et.
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS));
        }

        // Yeni kullanıcı oluştur.
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Varsayılan USER veriyoruz
                .build();

        userRepository.save(user);


        // Kullanıcı kaydolunca JWT token üretip dönebiliriz.
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_" + user.getRole().name())
                    .build()
        );



        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Kullanıcı başarıyla kaydedildi.")
                .build();
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
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


        // Kullanıcı veritabanında olmalı ve şifre doğru olmalı, yoksa authenticate() exception fırlatır.
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();


        // Kullanıcı doğrulandığına göre token üretip dönebiliriz.
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_" + user.getRole().name())
                    .build()
        );

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Kullanıcı başarıyla doğrulandı.")
                .build();
    }
}