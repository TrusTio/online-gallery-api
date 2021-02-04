package com.mine.gallery.service;

import com.mine.gallery.exception.role.RoleNotFoundException;
import com.mine.gallery.exception.user.SignUpValidationException;
import com.mine.gallery.exception.user.UserNotFoundException;
import com.mine.gallery.persistence.entity.Role;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.dto.SignupUserDTO;
import com.mine.gallery.service.mapper.UserMapper;
import com.mine.gallery.util.ExceptionStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Collections;

/**
 * Service class for the {@link com.mine.gallery.controller.UserController}
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
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Checks if the email and username are taken and throws an exception if it fails to.
     * Then validates them and throws an exception if it fails to.
     * Creates a new {@link User} and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.UserRepository}
     *
     * @param signupUserDTO The {@link SignupUserDTO} object to be added as User in the database
     * @return The {@link SignupUserDTO} object saved in the database as {@link User}
     */
    public SignupUserDTO signUp(SignupUserDTO signupUserDTO, Errors errors) {

        if (userRepository.existsByUsername(signupUserDTO.getUsername())) {
            throw new SignUpValidationException("Username taken!");
        }

        if (userRepository.existsByEmail(signupUserDTO.getEmail())) {
            throw new SignUpValidationException("Email taken!");
        }

        if (errors.hasErrors()) {
            String exceptionMessage = ExceptionStringUtil.exceptionMessageBuilder(errors);
            throw new SignUpValidationException(exceptionMessage);
        }

        // set new accounts to normal users by default
        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(RoleNotFoundException::new);

        User user = new User()
                .setUsername(signupUserDTO.getUsername())
                .setEmail(signupUserDTO.getEmail())
                .setPassword(bCryptPasswordEncoder.encode(signupUserDTO.getPassword()))
                .setRoles(Collections.singleton(role));

        return UserMapper.toSignupUserDto(userRepository.save(user));
    }

    /**
     * Adds ADMIN role to the user.
     *
     * @param userId Long id of the user
     */
    public void setAdmin(Long userId) {
        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(RoleNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.addRole(role);

        userRepository.save(user);
    }

    /**
     * Removes the ADMIN role from the user
     *
     * @param userId Long id of the user
     */
    public void removeAdmin(Long userId) {
        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(RoleNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.removeRole(role);

        userRepository.save(user);
    }
}
