package com.mine.gallery.service;

import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.entity.UserRole;
import com.mine.gallery.persistence.repository.UserRoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

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
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Creates a new {@link User User} and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.UserRepository UserRepository}
     *
     * @param userDTO The UserDTO object to be added as User in the databse
     * @return The UserDTO object saved in the database as User
     */
    public UserDTO signUp(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username taken!");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email taken!");
        }

        UserRole userRole = userRoleRepository.findByName(RoleName.ROLE_ADMIN).get();

        User user = new User()
                .setUsername(userDTO.getUsername())
                .setEmail(userDTO.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()))
                .setRoles(Collections.singleton(userRole));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            return UserMapper.toUserDto(userRepository.save(user));
        } else {
            Iterator<ConstraintViolation<User>> iterator = violations.iterator();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iterator.next().getMessage());
        }
    }
}
