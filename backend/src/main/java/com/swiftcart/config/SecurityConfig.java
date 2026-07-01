package com.swiftcart.config;

import com.swiftcart.jwt.JwtProperties;
import com.swiftcart.security.JwtAuthenticationFilter;
import com.swiftcart.security.RestAccessDeniedHandler;
import com.swiftcart.security.RestAuthenticationEntryPoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider,
                                            RestAuthenticationEntryPoint authenticationEntryPoint, RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/assets/**", "/uploads/**",
                                "/login", "/register", "/forgot-password",
                                "/products", "/products/**", "/categories", "/search",
                                "/dashboard", "/wishlist", "/cart", "/checkout", "/payment-success", "/payment-failed", "/orders", "/profile", "/addresses",
                                "/admin", "/admin/**", "/unauthorized",
                                "/api/v1", "/api/v1/swagger-ui.html", "/api/v1/swagger-ui/**", "/api/v1/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/actuator/health"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh-token", "/api/v1/auth/refresh", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password", "/api/v1/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/*/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**", "/api/v1/categories", "/api/v1/categories/**").permitAll()
                        .requestMatchers(
                                "/api/v1/admin/**",
                                "/api/v1/products/create",
                                "/api/v1/products/update/**",
                                "/api/v1/products/delete/**",
                                "/api/v1/categories/create",
                                "/api/v1/categories/update/**",
                                "/api/v1/categories/delete/**",
                                "/api/v1/users/**",
                                "/api/v1/orders/manage/**",
                                "/api/v1/payments/manage/**"
                        ).hasRole("ADMIN")
                        .requestMatchers("/api/v1/cart", "/api/v1/cart/**", "/api/v1/orders", "/api/v1/orders/**", "/api/v1/wishlist", "/api/v1/wishlist/**", "/api/v1/profile", "/api/v1/profile/**", "/api/v1/payments", "/api/v1/payments/**", "/api/v1/reviews", "/api/v1/reviews/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


