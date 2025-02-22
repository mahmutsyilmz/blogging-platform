package com.web.blog.security;

import com.web.blog.entity.User;
import com.web.blog.exception.UsernameNotFoundException;
import com.web.blog.exception.handler.ErrorMessage;
import com.web.blog.exception.handler.MessageType;
import com.web.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));

        return user;
    }
}