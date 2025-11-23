package io.appswave.joiner.util;

import io.appswave.joiner.dto.response.UserResponse;
import io.appswave.joiner.entity.User;

public class UserMapper {

    public static UserResponse toDto(User u) {
        return new UserResponse(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getUserRole().name()
        );
    }
}
