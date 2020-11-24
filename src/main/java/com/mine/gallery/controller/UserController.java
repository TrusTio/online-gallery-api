package com.mine.gallery.controller;

import com.mine.gallery.persistence.entity.User;
import com.mine.gallery.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/user")
public class UserController {

    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;
    @Autowired
    private com.mine.gallery.service.UserService userService;

    @GetMapping("/signin")
    String signIn() {

        return "sign-in";
    }

    @GetMapping("/signup")
    String signUp() {

        return "sign-up";
    }

    @PostMapping("/signup")
    public @ResponseBody
    String signUp(@RequestBody UserDTO user) {
        Logger.getLogger(UserController.class.getName()).warning("Created new user!");
        Logger.getLogger(UserController.class.getName()).warning(user.toString());
        userService.signUp(user);
        return "Signed up!";
    }

    @GetMapping(path = "/all/users")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        Logger.getLogger(UserController.class.getName()).warning("Fetched all users!");
        return userRepository.findAll();
    }
}
