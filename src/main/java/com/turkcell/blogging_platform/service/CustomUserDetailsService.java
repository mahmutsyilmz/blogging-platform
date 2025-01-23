package com.turkcell.blogging_platform.service;

import com.turkcell.blogging_platform.entity.User;
import com.turkcell.blogging_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1) Veritabanından kendi 'User' entity'nizi çekersiniz
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // 2) 'User' entity'niz -> 'UserDetails' nesnesine dönüştürülür
        return mapToUserDetails(user);
    }

    private UserDetails mapToUserDetails(User user) {

        String roleName = "ROLE_" + user.getRole().name(); // Örneğin "ROLE_ADMIN" ya da "ROLE_USER"

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(roleName)
                .build();
    }
}