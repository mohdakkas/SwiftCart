package com.swiftcart.service;

import com.swiftcart.dto.UserDtos.ChangePasswordRequest;
import com.swiftcart.dto.UserDtos.ProfileResponse;
import com.swiftcart.dto.UserDtos.UpdateProfileRequest;
import com.swiftcart.dto.UserDtos.UploadProfileImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    ProfileResponse profile(String email);
    ProfileResponse updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    UploadProfileImageResponse uploadProfileImage(String email, MultipartFile file) throws IOException;
    List<ProfileResponse> allUsers();
}


