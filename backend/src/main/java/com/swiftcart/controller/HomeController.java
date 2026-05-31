package com.swiftcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {
    @GetMapping("/api/v1")
    @ResponseBody
    public Map<String, Object> apiRoot() {
        return Map.of(
                "name", "SwiftCart API",
                "version", "1.0.0",
                "swagger", "/api/v1/swagger-ui.html",
                "health", "/actuator/health"
        );
    }
}
