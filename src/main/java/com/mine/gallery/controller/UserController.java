package com.mine.gallery.controller;

import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.UserService;
import com.mine.gallery.service.dto.ImageDTO;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.mapper.ImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
    private RoleRepository roleRepository;
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
    @Secured("ROLE_ADMIN")
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
    @GetMapping(path = "/{username}")
    public User getUserById(@PathVariable("username") String username, Principal principal) {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || username.equals(principal.getName())) {
            return userRepository.findByUsername(username);
        } else {
            throw new UnauthorizedAccessException("Access denied, you can't check other users details.");
        }
    }

    /**
     * A GET method that returns a list of the gallery names a specific user has, using his id
     * Users with role USER can access only their own user galleries.
     * Users with role ADMIN can access the galleries of everyone.
     *
     * @param id        Long id of the user
     * @param principal Principal
     * @return List<String> of the galleries
     */
    @GetMapping(path = "/{id}/galleries")
    public List<String> getUserGalleries(@PathVariable("id") Long id, Principal principal) {
        Logger.getLogger(UserController.class.getName()).info("Fetching user galleries!");
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || userRepository.findById(id).get().getUsername().equals(principal.getName())) {

            return userRepository.findById(id).get().getGalleries()
                    .stream().map(Gallery::getName)
                    .collect(Collectors.toList());

        } else {
            throw new UnauthorizedAccessException("Access denied, you can't check other users galleries.");
        }
    }

    /**
     * GET method that returns a list of the images(name and url) the user has in specific gallery
     * Users with role USER can access only their own user images.
     * Users with role ADMIN can access the images of everyone.
     *
     * @param id
     * @param galleryName
     * @param principal
     * @return
     */
    @GetMapping(path = "/{id}/gallery/{galleryName}")
    public List<ImageDTO> getUserGalleryImages(@PathVariable("id") Long id,
                                               @PathVariable("galleryName") String galleryName,
                                               Principal principal) {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || userRepository.findById(id).get().getUsername().equals(principal.getName())) {

            return galleryRepository.findByNameAndUserId(galleryName, id).get().getImages()
                    .stream().map(ImageMapper::toImageDTO)
                    .collect(Collectors.toList());
        } else {
            throw new UnauthorizedAccessException("Access denied, you can't check other users images.");
        }
    }
}
