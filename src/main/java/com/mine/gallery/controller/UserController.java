package com.mine.gallery.controller;

import com.mine.gallery.exception.gallery.GalleryNotFoundException;
import com.mine.gallery.exception.user.UserNotFoundException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.security.IdUsernamePasswordAuthenticationToken;
import com.mine.gallery.service.UserService;
import com.mine.gallery.service.dto.ImageDTO;
import com.mine.gallery.service.dto.SignupUserDTO;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.ImageMapper;
import com.mine.gallery.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * User controller that exposes user end points
 *
 * @author TrusTio
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GalleryRepository galleryRepository;


    /**
     * A POST method that accepts {@link SignupUserDTO UserDTO} body with it's parameters to create a new account in the database
     * using the signUp service from {@link com.mine.gallery.service.UserService UserService}
     *
     * @param user UserDTO object used to create new user
     * @return String confirming the sign up
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupUserDTO user, Errors errors) {
        userService.signUp(user, errors);
        Logger.getLogger(UserController.class.getName()).info("Created new user!");

        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    /**
     * A GET method that fetches all the users with their galleries and images
     * Only users with role ADMIN can access this endpoint.
     *
     * @return JSON object with all the user information
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/all/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * A PUT method that gives the user an ADMIN role to an user.
     * Only users with role ADMIN can access this endpoint.
     *
     * @param id Long id of the user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/admin")
    public ResponseEntity<String> setAdmin(@PathVariable("id") Long id) {
        userService.setAdmin(id);

        return new ResponseEntity<>("User updated to admin successfully", HttpStatus.ACCEPTED);
    }

    /**
     * A PUT method that removes ADMIN role from the user.
     * Only users with role ADMIN can access this endpoint.
     *
     * @param id Long id of the user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/noadmin")
    public ResponseEntity<String> removeAdmin(@PathVariable("id") Long id) {
        userService.removeAdmin(id);

        return new ResponseEntity<>("User updated to normal user successfully", HttpStatus.ACCEPTED);
    }

    /**
     * A GET method that fetches all the information about specific user using user id
     * Users with role USER can access only their own user information.
     * Users with role ADMIN can access information about all users.
     *
     * @param id             Long id of the user to be fetched
     * @param authentication IdUsernamePasswordAuthenticationToken holds information for the currently logged in user.
     * @return the found {@link User User} if such exists
     */
    @PreAuthorize("#id == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{id}")
    public UserDTO getUserById(@PathVariable("id") Long id,
                               @CurrentSecurityContext(expression = "authentication")
                                       IdUsernamePasswordAuthenticationToken authentication) {

        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    /**
     * A GET method that returns a list of the gallery names a specific user has, using his id
     * Users with role USER can access only their own user galleries.
     * Users with role ADMIN can access the galleries of everyone.
     *
     * @param id             Long id of the user to be fetched
     * @param authentication IdUsernamePasswordAuthenticationToken holds information for the currently logged in user.
     * @return List<String> of the galleries
     */
    @PreAuthorize("#id == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{id}/galleries")
    public List<String> getUserGalleries(@PathVariable("id") Long id,
                                         @CurrentSecurityContext(expression = "authentication")
                                                 IdUsernamePasswordAuthenticationToken authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return user.getGalleries()
                .stream().map(Gallery::getName)
                .collect(Collectors.toList());
    }

    /**
     * GET method that returns a list of the images(name and url) the user has in specific gallery
     * Users with role USER can access only their own user images.
     * Users with role ADMIN can access the images of everyone.
     *
     * @param id             Long id of the user to be fetched
     * @param galleryName    String gallery name
     * @param authentication IdUsernamePasswordAuthenticationToken holds information for the currently logged in user.
     * @return List<ImageDTO>
     */
    @PreAuthorize("#id == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{id}/gallery/{galleryName}")
    public List<ImageDTO> getUserGalleryImages(@PathVariable("id") Long id,
                                               @PathVariable("galleryName") String galleryName,
                                               @CurrentSecurityContext(expression = "authentication")
                                                       IdUsernamePasswordAuthenticationToken authentication) {
        Gallery gallery = galleryRepository.findByNameAndUserId(galleryName, id)
                .orElseThrow(() -> new GalleryNotFoundException(galleryName));

        return gallery.getImages()
                .stream().map(ImageMapper::toImageDTO)
                .collect(Collectors.toList());
    }
}