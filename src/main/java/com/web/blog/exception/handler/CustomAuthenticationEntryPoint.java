package com.web.blog.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
        response.setContentType("application/json; charset=UTF-8");

        ApiError<String> apiError = new ApiError<>();
        apiError.setStatus(HttpStatus.UNAUTHORIZED.value()); //401

        // benim tanımladığım exception'a dönüştürüyoruz
        Exception<String> exception = new Exception<>();
        exception.setCreatedDate(new Date());
        exception.setHostName(getHostName());
        exception.setPath("authenticationEntryPoint");
        exception.setMessage("You are not authorized to access this resource.");

        apiError.setException(exception);

        String jsonBody = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(jsonBody);
        response.getWriter().flush();
    }

    // hostname bilgisi
    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }
}