package com.swiftcart.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftcart.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws java.io.IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), new ErrorResponse(Instant.now(), 401, "Unauthorized", "Authentication is required", request.getRequestURI(), Map.of()));
    }
}
