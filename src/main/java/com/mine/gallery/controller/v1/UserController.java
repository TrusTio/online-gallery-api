package com.mine.gallery.controller.v1;

import com.mine.gallery.security.IdUsernamePasswordAuthenticationToken;
import com.mine.gallery.service.GalleryService;
import com.mine.gallery.service.ImageService;
import com.mine.gallery.service.UserService;
import com.mine.gallery.service.dto.ImageDTO;
import com.mine.gallery.service.dto.SignupUserDTO;
import com.mine.gallery.service.dto.UserDTO;
import com.mine.gallery.service.dto.UserGalleriesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * User controller that exposes user end points
 *
 * @author TrusTio
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private GalleryService galleryService;
    @Autowired
    private ImageService imageService;


    /**
     * A POST method that accepts {@link SignupUserDTO} body with it's parameters to create a new account in the database
     * using the signUp service from {@link com.mine.gallery.service.UserService}
     *
     * @param user {@link SignupUserDTO} body object used to create new user
     * @return String confirming the sign up
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupUserDTO user, Errors errors) {
        userService.signUp(user, errors);
        log.info("User created successfully!");

        return new ResponseEntity<>("User created successfully!", HttpStatus.CREATED);
    }

    /**
     * A GET method that fetches all the users paginated, sorted and mapped to {@link UserDTO}.
     * Only users with role ADMIN can access this endpoint.
     *
     * @param pageNo   Integer Number of the page to be fetched
     * @param pageSize Integer Size of the pages
     * @param sortBy   String sort by field
     * @return {@link List<UserDTO>} containing the user data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<UserDTO> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(defaultValue = "id") String sortBy) {
        log.info("Users fetched successfully!");

        return userService.getAllUsers(pageNo, pageSize, sortBy);
    }

    /**
     * A PATCH method that gives the user an ADMIN role to an user.
     * Only users with role ADMIN can access this endpoint.
     *
     * @param userId Long id of the user
     * @return {@link ResponseEntity<String>}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}/admin")
    public ResponseEntity<String> setAdmin(@PathVariable("userId") Long userId) {
        userService.setAdmin(userId);
        log.info("User updated to admin successfully!");

        return new ResponseEntity<>("User updated to admin successfully!", HttpStatus.ACCEPTED);
    }

    /**
     * A PATCH method that removes ADMIN role from the user.
     * Only users with role ADMIN can access this endpoint.
     *
     * @param userId Long id of the user
     * @return {@link ResponseEntity<String>}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}/noadmin")
    public ResponseEntity<String> removeAdmin(@PathVariable("userId") Long userId) {
        userService.removeAdmin(userId);
        log.info("User updated to normal user successfully!");

        return new ResponseEntity<>("User updated to normal user successfully!", HttpStatus.ACCEPTED);
    }

    /**
     * A GET method that fetches all the information about specific user using user id
     * Users with role USER can access only their own user information.
     * Users with role ADMIN can access information about all users.
     *
     * @param userId         Long id of the user to be fetched
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds information for the currently logged in user.
     * @return the found {@link UserDTO} if such exists
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{userId}")
    public UserDTO getUserById(@PathVariable("userId") Long userId,
                               @CurrentSecurityContext(expression = "authentication")
                                       IdUsernamePasswordAuthenticationToken authentication) {
        log.info("User fetched successfully!");

        return userService.getUserById(userId);
    }

    /**
     * A GET method that fetches data for the currently logged in user.
     *
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds information for the currently logged in user.
     * @return the found {@link UserDTO} if such exists
     */
    @GetMapping(path = "/me")
    public UserDTO getCurrentUser(@CurrentSecurityContext(expression = "authentication")
                                          IdUsernamePasswordAuthenticationToken authentication) {
        log.info("Current User fetched successfully!");

        return userService.getUserById(authentication.getId());
    }

    /**
     * A GET method that returns a list of the gallery names a specific user has, using his id
     * Users with role USER can access only their own user galleries.
     * Users with role ADMIN can access the galleries of everyone.
     *
     * @param pageNo         Integer Number of the page to be fetched
     * @param pageSize       Integer Size of the pages
     * @param sortBy         String sort by field
     * @param userId         Long id of the user to be fetched
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds information for the currently logged in user.
     * @return {@link List<UserGalleriesDTO>} of the gallery names
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{userId}/galleries")
    public List<UserGalleriesDTO> getUserGalleries(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "20") Integer pageSize,
                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                   @PathVariable("userId") Long userId,
                                                   @CurrentSecurityContext(expression = "authentication")
                                                           IdUsernamePasswordAuthenticationToken authentication) {
        log.info("User Galleries fetched successfully!");

        return galleryService.getUserGalleries(pageNo, pageSize, sortBy, userId);
    }

    /**
     * GET method that returns a list of the images(id, name and url) the user has in specific gallery
     * Users with role USER can access only their own user images.
     * Users with role ADMIN can access the images of everyone.
     *
     * @param pageNo         Integer Number of the page to be fetched
     * @param pageSize       Integer Size of the pages
     * @param sortBy         String sort by field
     * @param userId         Long id of the user to be fetched
     * @param galleryId      Long id of the gallery
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds information for the currently logged in user.
     * @return {@link List<ImageDTO>}
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{userId}/galleries/{galleryId}")
    public List<ImageDTO> getUserGalleryImages(@RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "20") Integer pageSize,
                                               @RequestParam(defaultValue = "id") String sortBy,
                                               @PathVariable("userId") Long userId,
                                               @PathVariable("galleryId") Long galleryId,
                                               @CurrentSecurityContext(expression = "authentication")
                                                       IdUsernamePasswordAuthenticationToken authentication) {
        log.info("User Images in Gallery fetched successfully!");

        return imageService.getUserGalleryImages(pageNo, pageSize, sortBy, userId, galleryId);
    }

    /**
     * Get method that returns a list of the images(id, name and url) the user has in all galleries.
     * Users with role USER can access only their own user images.
     * Users with role ADMIN can access the images of everyone.
     *
     * @param pageNo         Integer Number of the page to be fetched
     * @param pageSize       Integer Size of the pages
     * @param sortBy         String sort by field
     * @param userId         Long id of the user to be fetched
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds information for the currently logged in user.
     * @return {@link List<ImageDTO>}
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{userId}/images")
    public List<ImageDTO> getUserImages(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "20") Integer pageSize,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @PathVariable("userId") Long userId,
                                        @CurrentSecurityContext(expression = "authentication")
                                                IdUsernamePasswordAuthenticationToken authentication) {
        log.info("User Images fetched successfully!");

        return imageService.getUserImages(pageNo, pageSize, sortBy, userId);
    }
}