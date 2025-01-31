package com.turkcell.blog.config;

import com.turkcell.blog.security.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;
    private final ApplicationConfig applicationConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Token doğrulama işlemleri
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Header yoksa veya "Bearer " ile başlamıyorsa filter'a devam.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer "ın ardından gelen token'ı alalım.
        jwt = authHeader.substring(7);
        username = jwtServiceImpl.extractUsername(jwt); // token içinden username'i çıkardık.

        // Kullanıcı adı varsa ve SecurityContext boşsa doğrulamaya çalış.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = applicationConfig.userDetailsService().loadUserByUsername(username);
            if (jwtServiceImpl.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Kullanıcıyı security context'e yerleştir
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Filtre zincirine devam
        filterChain.doFilter(request, response);
    }
}