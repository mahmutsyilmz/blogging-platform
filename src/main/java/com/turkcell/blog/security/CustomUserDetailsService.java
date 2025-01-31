package com.turkcell.blog.security;

import com.turkcell.blog.entity.User;
import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.UserRepository;
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