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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {

        return http
            // 1. Process CORS preflight requests first using CorsConfigurationSource
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // 2. Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())

            // 3. Stateless session management (JWT based)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4. Custom authentication provider and exception handling
            .authenticationProvider(authenticationProvider)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            // 5. Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow all CORS preflight OPTIONS requests unconditionally
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Static resources and frontend routes
                .requestMatchers(
                    "/", "/index.html", "/assets/**", "/uploads/**",
                    "/login", "/register", "/forgot-password",
                    "/products", "/products/**", "/categories", "/search",
                    "/dashboard", "/wishlist", "/cart", "/checkout", "/payment-success", "/payment-failed", "/orders", "/profile",
                    "/admin", "/admin/**", "/unauthorized",
                    "/api/v1", "/api/v1/swagger-ui.html", "/api/v1/swagger-ui/**", "/api/v1/v3/api-docs/**", "/swagger-ui/**"
                ).permitAll()

                // Public auth endpoints
                .requestMatchers(
                    "/auth/**",
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh-token",
                    "/api/v1/auth/refresh"
                ).permitAll()

                // Public catalog endpoints (GET)
                .requestMatchers(HttpMethod.GET, "/api/v1/products/*/reviews").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**", "/api/v1/categories", "/api/v1/categories/**").permitAll()

                // Admin endpoints
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

                // Authenticated user endpoints
                .requestMatchers(
                    "/api/v1/cart", "/api/v1/cart/**",
                    "/api/v1/orders", "/api/v1/orders/**",
                    "/api/v1/wishlist", "/api/v1/wishlist/**"
                ).authenticated()

                // Require authentication for anything else
                .anyRequest().authenticated()
            )

            // 6. Register JWT filter before standard username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}