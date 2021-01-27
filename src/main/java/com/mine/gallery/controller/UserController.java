package com.mine.gallery.controller;

import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.UserService;
import com.mine.gallery.service.dto.ImageDTO;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.ImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
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
     * A POST method that accepts {@link UserDTO UserDTO} body with it's parameters to create a new account in the database
     * using the signUp service from {@link com.mine.gallery.service.UserService UserService}
     *
     * @param user UserDTO object used to create new user
     * @return String confirming the sign up
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserDTO user, Errors errors) {
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
    public Iterable<User> getAllUsers(Principal principal) {
        Logger.getLogger(UserController.class.getName()).info("Fetched all users!");
        return userRepository.findAll();
    }

    /**
     * A GET method that fetches all the information about specific user using user id
     * Users with role USER can access only their own user information.
     * Users with role ADMIN can access information about all users.
     *
     * @param username the username of the user to be fetched
     * @return the found {@link User User} if such exists
     */
    @PreAuthorize("#username == #principal.name || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{username}")
    public User getUserByUsername(@PathVariable("username") String username, Principal principal) {
        return userRepository.findByUsername(username);
    }

    /**
     * A GET method that returns a list of the gallery names a specific user has, using his username
     * Users with role USER can access only their own user galleries.
     * Users with role ADMIN can access the galleries of everyone.
     *
     * @param username  String username of the user
     * @param principal Principal
     * @return List<String> of the galleries
     */
    @PreAuthorize("#username == #principal.name || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{username}/galleries")
    public List<String> getUserGalleries(@PathVariable("username") String username, Principal principal) {
        Logger.getLogger(UserController.class.getName()).info("Fetching user galleries!");

        return userRepository.findByUsername(username).getGalleries()
                .stream().map(Gallery::getName)
                .collect(Collectors.toList());
    }

    /**
     * GET method that returns a list of the images(name and url) the user has in specific gallery
     * Users with role USER can access only their own user images.
     * Users with role ADMIN can access the images of everyone.
     *
     * @param username    String username of the user
     * @param galleryName String gallery name
     * @param principal   Principal
     * @return List<ImageDTO>
     */
    @PreAuthorize("#username == #principal.name || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{username}/gallery/{galleryName}")
    public List<ImageDTO> getUserGalleryImages(@PathVariable("username") String username,
                                               @PathVariable("galleryName") String galleryName,
                                               Principal principal) {

        return galleryRepository.findByNameAndUserId(galleryName, userRepository.findByUsername(username).getId()).get()
                .getImages()
                .stream().map(ImageMapper::toImageDTO)
                .collect(Collectors.toList());
    }
}