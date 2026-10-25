package com.swiftcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller
public class SpaForwardController {
    @GetMapping({
            "/",
            "/login",
            "/register",
            "/forgot-password",
            "/products",
            "/products/{id}",
            "/categories",
            "/search",
            "/dashboard",
            "/wishlist",
            "/cart",
            "/checkout",
            "/payment-success",
            "/payment-failed",
            "/orders",
            "/profile",
            "/addresses",
            "/admin",
            "/admin/**",
            "/unauthorized"
    })
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}
