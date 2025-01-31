package com.turkcell.blog.controller;

import com.turkcell.blog.dto.request.LoginDtoRequest;
import com.turkcell.blog.dto.request.RegisterDtoRequest;
import com.turkcell.blog.dto.response.AuthenticationDtoResponse;
import com.turkcell.blog.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationDtoResponse> register(
            @Valid @RequestBody RegisterDtoRequest request
    ) {
        AuthenticationDtoResponse response = userServiceImpl.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDtoResponse> login(
            @Valid @RequestBody LoginDtoRequest request
    ) {
        AuthenticationDtoResponse response = userServiceImpl.authenticate(request);
        return ResponseEntity.ok(response);
    }


}