package com.mine.gallery.service;

import com.mine.gallery.exception.role.RoleNotFoundException;
import com.mine.gallery.exception.user.SignUpValidationException;
import com.mine.gallery.exception.user.UserNotFoundException;
import com.mine.gallery.persistence.entity.Role;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.dto.SignupUserDTO;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.UserMapper;
import com.mine.gallery.util.ExceptionStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for User related methods.
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
    private GalleryRepository galleryRepository;
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
     * Fetches all the users paginated, sorted and mapped to {@link UserDTO}.
     *
     * @param pageNo   Integer Number of the page to be fetched
     * @param pageSize Integer Size of the pages
     * @param sortBy   String sort by field
     * @return {@link List<UserDTO>} containing the user data
     */
    public List<UserDTO> getAllUsers(Integer pageNo, Integer pageSize, String sortBy) {
        return userRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)))
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
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

    /**
     * Fetches all the information about specific user using user id
     *
     * @param userId Long id of the user to be fetched
     * @return the found {@link UserDTO} if such exists
     */
    public UserDTO getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));
    }
}
