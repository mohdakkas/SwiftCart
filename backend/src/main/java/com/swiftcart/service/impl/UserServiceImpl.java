package com.swiftcart.service.impl;

import com.swiftcart.dto.UserDtos.ChangePasswordRequest;
import com.swiftcart.dto.UserDtos.ProfileResponse;
import com.swiftcart.dto.UserDtos.UpdateProfileRequest;
import com.swiftcart.dto.UserDtos.UploadProfileImageResponse;
import com.swiftcart.entity.User;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.mapper.UserMapper;
import com.swiftcart.repository.RefreshTokenRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse profile(String email) {
        return userMapper.toProfile(user(email));
    }

    @Override
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = user(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setProfileImageUrl(request.profileImageUrl());
        return userMapper.toProfile(user);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = user(email);
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenRepository.revokeAllForUser(email);
    }

    @Override
    public UploadProfileImageResponse uploadProfileImage(String email, MultipartFile file) throws IOException {
        User user = user(email);
        String safeName = file.getOriginalFilename() == null ? "profile-image" : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
        String url = "/uploads/profile/" + user.getId() + "/" + safeName;
        user.setProfileImageUrl(url);
        file.getBytes();
        return new UploadProfileImageResponse(url);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponse> allUsers() {
        return userRepository.findAll().stream().map(userMapper::toProfile).toList();
    }

    private User user(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
