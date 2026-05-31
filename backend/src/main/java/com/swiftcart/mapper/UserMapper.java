package com.swiftcart.mapper;

import com.swiftcart.dto.AuthDtos.UserSummary;
import com.swiftcart.dto.UserDtos.ProfileResponse;
import com.swiftcart.entity.Role;
import com.swiftcart.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserSummary toSummary(User user) {
        String role = primaryRole(user);
        return new UserSummary(
                user.getId(),
                user.getEmail(),
                fullName(user),
                user.getFirstName(),
                user.getLastName(),
                role,
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    public ProfileResponse toProfile(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getProfileImageUrl(),
                user.isEmailVerified(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    private String fullName(User user) {
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    private String primaryRole(User user) {
        if (user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()))) {
            return "ROLE_ADMIN";
        }
        return "ROLE_USER";
    }
}
