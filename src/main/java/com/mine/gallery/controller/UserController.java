package com.mine.gallery.controller;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


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

    /**
     * A POST method that accepts {@link UserDTO UserDTO} body with it's parameters to create a new account in the database
     * using the signUp service from {@link com.mine.gallery.service.UserService UserService}
     *
     * @param user UserDTO object used to create new user
     * @return String confirming the sign up
     */
    @PostMapping("/signup")
    public @ResponseBody
    String signUp(@RequestBody UserDTO user) {
        userService.signUp(user);
        Logger.getLogger(UserController.class.getName()).info("Created new user!");
        return "Signed up!";
    }

    //TODO: Restrict only to admin

    /**
     * A GET method that fetches all the users with their galleries and images
     *
     * @return JSON object with all the user information
     */
    @GetMapping(path = "/all/users")
    public @ResponseBody
    Iterable<User> getAllUsers(Principal principal) {
        Logger.getLogger(UserController.class.getName()).info("Fetched all users!");
        return userRepository.findAll();
    }

    //TODO: check if the user is ADMIN or USER and implement the security based on that

    /**
     * A GET method that fetches all the information about specific user using user id
     *
     * @param id the user id of the user to be fetched
     * @return the found {@link User User} if such exists
     */
    @GetMapping(path = "/{id}")
    public @ResponseBody
    Optional<User> getUserById(@PathVariable("id") Long id, Principal principal) {
        if (userRepository.findById(id).get().getUsername().equals(principal.getName())) {
            return userRepository.findById(id);
        } else {
            return Optional.empty();
        }
    }
}
