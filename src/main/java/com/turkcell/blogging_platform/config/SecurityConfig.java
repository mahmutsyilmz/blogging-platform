package com.turkcell.blogging_platform.config;


import com.turkcell.blogging_platform.exception.handler.CustomAuthenticationEntryPoint;
import com.turkcell.blogging_platform.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT ile kimlik doğrulama yapacağımız için session kullanmayacağız
                // Stateless bir yapı oluşturuyoruz.
                // Bu sayede her istek için token kontrolü yapacağız.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // /api/auth/** ve /api/admin/** endpoint'leri herkes tarafından erişilebilir.
                        .requestMatchers("/api/auth/**",
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()
                        // /api/admin/** endpoint'leri sadece ADMIN rolüne sahip kullanıcılar tarafından erişilebilir.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // /api/user/** endpoint'leri sadece USER rolüne sahip kullanıcılar tarafından erişilebilir.
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandler ->exceptionHandler
                        .authenticationEntryPoint(authenticationEntryPoint))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder); // Bcrypt, vs.
        return provider;
    }
}