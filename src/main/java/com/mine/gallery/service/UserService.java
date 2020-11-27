package com.mine.gallery.service;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for the {@link com.mine.gallery.controller.UserController UserController}
 *
 * @author TrusTio
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserService {
    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;

    /**
     * Creates a new {@link User User} and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.UserRepository UserRepository}
     *
     * @param userDTO The UserDTO object to be added as User in the databse
     * @return The UserDTO object saved in the database as User
     */
    public UserDTO signUp(UserDTO userDTO) {
        User user = new User()
                .setUsername(userDTO.getUsername())
                .setEmail(userDTO.getEmail())
                .setPassword(userDTO.getPassword());
        
        return UserMapper.toUserDto(userRepository.save(user));
    }
}
