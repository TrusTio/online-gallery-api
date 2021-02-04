package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.SignupUserDTO;
import com.mine.gallery.service.dto.UserDTO;
import org.springframework.stereotype.Component;

/**
 * Maps the {@link User} object to a {@link SignupUserDTO} object
 *
 * @author TrusTio
 */
@Component
public class UserMapper {
    /**
     * Returns new {@link SignupUserDTO} object created from the {@link User} parameter
     *
     * @param user {@link User} object to be mapped to UserDTO object
     * @return {@link SignupUserDTO} object with username, email and password
     */
    public static SignupUserDTO toSignupUserDto(User user) {
        return new SignupUserDTO()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());
    }

    /**
     * Returns new {@link UserDTO} object mapped from {@link User} parameter
     *
     * @param user {@link User} object to be mapped to UserDTO object
     * @return {@link UserDTO} object with username, email and password
     */
    public static UserDTO toUserDto(User user) {
        return new UserDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setRoles(user.getRoles());
    }
}
