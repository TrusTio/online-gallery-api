package com.mine.gallery.controller;

import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * User controller that exposes user end points
 *
 * @author TrusTio
 */
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/user")
public class UserController {

    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;
    @Autowired
    private com.mine.gallery.service.UserService userService;
    @Autowired
    private com.mine.gallery.persistence.repository.RoleRepository roleRepository;

    /**
     * A POST method that accepts {@link UserDTO UserDTO} body with it's parameters to create a new account in the database
     * using the signUp service from {@link com.mine.gallery.service.UserService UserService}
     *
     * @param user UserDTO object used to create new user
     * @return String confirming the sign up
     */
    @PostMapping("/signup")
    public @ResponseBody
    String signUp(@Valid @RequestBody UserDTO user, Errors errors) {
        userService.signUp(user, errors);
        Logger.getLogger(UserController.class.getName()).info("Created new user!");
        return "Signed up!";
    }

    /**
     * A GET method that fetches all the users with their galleries and images
     * Only users with role ADMIN can access this endpoint.
     *
     * @return JSON object with all the user information
     */
    @Secured("ROLE_ADMIN")
    @GetMapping(path = "/all/users")
    public @ResponseBody
    Iterable<User> getAllUsers(Principal principal) {
        Logger.getLogger(UserController.class.getName()).info("Fetched all users!");
        return userRepository.findAll();
    }

    /**
     * A GET method that fetches all the information about specific user using user id
     * Users with role USER can access only their own user information.
     * Users with role ADMIN can access information about all users.
     *
     * @param id the user id of the user to be fetched
     * @return the found {@link User User} if such exists
     */
    @GetMapping(path = "/{id}")
    public @ResponseBody
    Optional<User> getUserById(@PathVariable("id") Long id, Principal principal) {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || userRepository.findById(id).get().getUsername().equals(principal.getName())) {
            return userRepository.findById(id);
        } else {
            throw new UnauthorizedAccessException("Access denied, you can't check other users details.");
        }
    }
    //TODO: Add Endpoint that returns only all the galleries of the user /{id}/galleries
    //TODO: Add Endpoint that returns only all the images in a given gallery of the user /{id}/gallery/{galleryname}
}
