package com.mine.gallery.service;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserService {
    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;

    public UserDTO signUp(UserDTO userDTO){

        User user = new User()
                .setUsername(userDTO.getUsername())
                .setEmail(userDTO.getEmail())
                .setPassword(userDTO.getPassword());
        return UserMapper.toUserDto(userRepository.save(user));
    }
}
