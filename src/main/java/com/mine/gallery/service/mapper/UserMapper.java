package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.SignupUserDTO;
import org.springframework.stereotype.Component;

/**
 * Maps the {@link User User} object to a {@link SignupUserDTO UserDTO} object
 *
 * @author TrusTio
 */
@Component
public class UserMapper {
    /**
     * Returns new {@link SignupUserDTO} object created from the {@link User User} parameter
     *
     * @param user User object to be mapped to UserDTO object
     * @return SignupUserDTO object with username, email and password
     */
    public static SignupUserDTO toSignupUserDto(User user) {
        return new SignupUserDTO()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());
    }
}
