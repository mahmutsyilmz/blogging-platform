package com.turkcell.blog.config;




import com.turkcell.blog.exception.UsernameNotFoundException;
import com.turkcell.blog.exception.handler.ErrorMessage;
import com.turkcell.blog.exception.handler.MessageType;
import com.turkcell.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /*
     Kullanıcıyı veritabanından bulup UserDetails döndüren servis.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(this::mapToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(new ErrorMessage(MessageType.USER_NOT_FOUND)));
    }

    private UserDetails mapToUserDetails(com.turkcell.blog.entity.User user) {
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().getName())
                .build();
    }

    /*
      Şifrelerin encode edilmesinde kullanılacak Bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
      AuthenticationManager Bean’i (Spring Security'nin kimlik doğrulama için kullandığı mekanizma).
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}