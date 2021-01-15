package com.mine.gallery.service;

import com.mine.gallery.exception.user.SignUpValidationException;
import com.mine.gallery.persistence.entity.Role;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.UserMapper;
import com.mine.gallery.util.ExceptionStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;

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
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Checks if the email and username are taken and throws an exception if it fails to.
     * Then validates them and throws an exception if it fails to.
     * Creates a new {@link User User} and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.UserRepository UserRepository}
     *
     * @param userDTO The UserDTO object to be added as User in the databse
     * @return The UserDTO object saved in the database as User
     */
    public UserDTO signUp(UserDTO userDTO, Errors errors) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new SignUpValidationException("Username taken!");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new SignUpValidationException("Email taken!");
        }

        if (errors.hasErrors()) {
            String exceptionMessage = ExceptionStringUtil.exceptionMessageBuilder(errors);
            throw new SignUpValidationException(exceptionMessage);
        }

        // set new accounts to normal users by default
        Role role = roleRepository.findByName(RoleName.ROLE_USER).get();

        User user = new User()
                .setUsername(userDTO.getUsername())
                .setEmail(userDTO.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .setPassword(userDTO.getPassword())
                .setRoles(Collections.singleton(role));

        return UserMapper.toUserDto(userRepository.save(user));

    }
}
