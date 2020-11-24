package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserDTO toUserDto(User user) {
        return new UserDTO()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());

    }
}
