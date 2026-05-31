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
public class RestAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws java.io.IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), new ErrorResponse(Instant.now(), 403, "Forbidden", "You do not have permission to access this resource", request.getRequestURI(), Map.of()));
    }
}
