package com.swiftcart.controller;

import com.swiftcart.dto.ApiResponse;
import com.swiftcart.dto.UserDtos.ChangePasswordRequest;
import com.swiftcart.dto.UserDtos.ProfileResponse;
import com.swiftcart.dto.UserDtos.UpdateProfileRequest;
import com.swiftcart.dto.UserDtos.UploadProfileImageResponse;
import com.swiftcart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/profile")
    public ApiResponse<ProfileResponse> profile(Authentication authentication) {
        return ApiResponse.ok("Profile loaded", userService.profile(authentication.getName()));
    }

    @PutMapping("/api/v1/profile")
    public ApiResponse<ProfileResponse> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(authentication.getName(), request));
    }

    @PatchMapping("/api/v1/profile/change-password")
    public ApiResponse<Void> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ApiResponse.ok("Password changed successfully", null);
    }

    @PostMapping("/api/v1/profile/image")
    public ApiResponse<UploadProfileImageResponse> uploadProfileImage(Authentication authentication, @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.ok("Profile image uploaded", userService.uploadProfileImage(authentication.getName(), file));
    }

    @GetMapping("/api/v1/users")
    public ApiResponse<List<ProfileResponse>> users() {
        return ApiResponse.ok("Users loaded", userService.allUsers());
    }
}
