package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import org.springframework.stereotype.Component;

/**
 * Maps the {@link User User} object to a {@link UserDTO UserDTO} object
 *
 * @author TrusTio
 */
@Component
public class UserMapper {
    /**
     * Returns new {@link UserDTO UserDTO} object created from the {@link User User} parameter
     *
     * @param user User object to be mapped to UserDTO object
     * @return UserDTO object with username, email and password
     */
    public static UserDTO toUserDto(User user) {
        return new UserDTO()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());

    }
}
