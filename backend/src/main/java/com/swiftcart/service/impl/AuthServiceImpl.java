package com.swiftcart.service.impl;

import com.swiftcart.dto.AuthDtos.AuthResponse;
import com.swiftcart.dto.AuthDtos.ForgotPasswordRequest;
import com.swiftcart.dto.AuthDtos.LoginRequest;
import com.swiftcart.dto.AuthDtos.LogoutRequest;
import com.swiftcart.dto.AuthDtos.RefreshRequest;
import com.swiftcart.dto.AuthDtos.RegisterRequest;
import com.swiftcart.dto.AuthDtos.ResetPasswordRequest;
import com.swiftcart.dto.AuthDtos.VerifyEmailRequest;
import com.swiftcart.entity.Cart;
import com.swiftcart.entity.RefreshToken;
import com.swiftcart.entity.Role;
import com.swiftcart.entity.User;
import com.swiftcart.entity.Wishlist;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.jwt.JwtService;
import com.swiftcart.mapper.UserMapper;
import com.swiftcart.repository.RoleRepository;
import com.swiftcart.repository.RefreshTokenRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.security.SwiftCartUserDetailsService;
import com.swiftcart.service.AuthService;
import com.swiftcart.util.TokenGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SwiftCartUserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService,
                           SwiftCartUserDetailsService userDetailsService, UserMapper userMapper,
                           RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered");
        }
        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException("Password and confirm password do not match");
        }
        Role customer = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new BusinessException("User role missing"));
        String[] names = splitName(request.fullName(), request.firstName(), request.lastName());
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(names[0]);
        user.setLastName(names[1]);
        user.setPhone(request.phone());
        user.setEmailVerified(false);
        user.setEmailVerificationToken(TokenGenerator.opaqueToken());
        user.setEmailVerificationTokenExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        user.getRoles().add(customer);

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        user.setWishlist(wishlist);

        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        return issueTokens(userRepository.findByEmail(email).orElseThrow());
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(TokenGenerator.sha256(request.refreshToken()))
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        if (!refreshToken.isUsable()) {
            throw new BusinessException("Invalid refresh token");
        }
        refreshToken.setRevoked(true);
        return issueTokens(refreshToken.getUser());
    }

    @Override
    public void logout(String email, LogoutRequest request) {
        refreshTokenRepository.findByTokenHash(TokenGenerator.sha256(request.refreshToken()))
                .filter(token -> token.getUser().getEmail().equalsIgnoreCase(email))
                .ifPresent(token -> token.setRevoked(true));
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email().toLowerCase()).ifPresent(user -> {
            user.setPasswordResetToken(TokenGenerator.opaqueToken());
            user.setPasswordResetTokenExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        });
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.token())
                .orElseThrow(() -> new BusinessException("Invalid reset token"));
        if (user.getPasswordResetTokenExpiresAt() == null || user.getPasswordResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Reset token expired");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        refreshTokenRepository.revokeAllForUser(user.getEmail());
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmailVerificationToken(request.token())
                .orElseThrow(() -> new BusinessException("Invalid verification token"));
        if (user.getEmailVerificationTokenExpiresAt() == null || user.getEmailVerificationTokenExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Verification token expired");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
    }

    private AuthResponse issueTokens(User user) {
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String refreshToken = TokenGenerator.opaqueToken();
        RefreshToken storedToken = new RefreshToken();
        storedToken.setUser(user);
        storedToken.setTokenHash(TokenGenerator.sha256(refreshToken));
        storedToken.setExpiresAt(Instant.now().plus(14, ChronoUnit.DAYS));
        refreshTokenRepository.save(storedToken);
        var summary = userMapper.toSummary(user);
        return new AuthResponse(jwtService.accessToken(details), refreshToken, summary.role(), summary);
    }

    private String[] splitName(String fullName, String firstName, String lastName) {
        if (firstName != null && !firstName.isBlank()) {
            return new String[]{firstName.trim(), lastName == null || lastName.isBlank() ? "" : lastName.trim()};
        }
        String normalized = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        int split = normalized.indexOf(' ');
        if (split < 0) {
            return new String[]{normalized, ""};
        }
        return new String[]{normalized.substring(0, split), normalized.substring(split + 1)};
    }
}
