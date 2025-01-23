package com.turkcell.blogging_platform.service;


import com.turkcell.blogging_platform.dto.request.LoginRequest;
import com.turkcell.blogging_platform.dto.request.RegisterRequest;
import com.turkcell.blogging_platform.dto.response.AuthenticationResponse;
import com.turkcell.blogging_platform.entity.Role;
import com.turkcell.blogging_platform.entity.User;
import com.turkcell.blogging_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
                .build();
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        // Kullanıcıdan gelen username ve password'ü doğrulamak için
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Kullanıcı veritabanında olmalı ve şifre doğru olmalı, yoksa authenticate() exception fırlatır.
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

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
                .build();
    }
}