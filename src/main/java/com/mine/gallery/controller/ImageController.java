package com.mine.gallery.controller;

import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.security.IdUsernamePasswordAuthenticationToken;
import com.mine.gallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image controller that exposes gallery end points
 *
 * @author TrusTio
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/image")
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    /**
     * A POST method that lets the user upload an image
     * to his own gallery.
     *
     * @param image          MultipartFile image to be uploaded
     * @param galleryName    String name of the gallery
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@RequestParam("imageFile") MultipartFile image,
                                              @RequestParam("galleryName") String galleryName,
                                              @CurrentSecurityContext(expression = "authentication")
                                                      IdUsernamePasswordAuthenticationToken authentication) {
        imageService.save(image,
                galleryName,
                authentication.getId());

        return new ResponseEntity<>("Image uploaded successfully", HttpStatus.CREATED);
    }

    /**
     * A GET method that fetches a specific image.
     * Users with role USER can access only their own images.
     * Users with role ADMIN can access all images.
     *
     * @param userId         Long id of the user of the image
     * @param galleryName    String name of the gallery
     * @param imageName      String name of the image
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return FileSystemResource
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{userId}/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public FileSystemResource retrieveImage(@PathVariable("userId") Long userId,
                                            @PathVariable("galleryName") String galleryName,
                                            @PathVariable("imageName") String imageName,
                                            @CurrentSecurityContext(expression = "authentication")
                                                    IdUsernamePasswordAuthenticationToken authentication) {

        return imageService.find(userId, galleryName, imageName);
    }

    /**
     * DELETE method that deletes a specific image
     * Users with role USER can delete only their own images.
     * Users with role ADMIN can delete any images.
     *
     * @param userId         Long id of the user of the image
     * @param galleryName    String name of the gallery
     * @param imageName      String name of the image
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{userId}/{galleryName}/{imageName}")
    public ResponseEntity<String> deleteImage(@PathVariable("userId") Long userId,
                                              @PathVariable("galleryName") String galleryName,
                                              @PathVariable("imageName") String imageName,
                                              @CurrentSecurityContext(expression = "authentication")
                                                      IdUsernamePasswordAuthenticationToken authentication) {

        imageService.deleteImage(userId, galleryName, imageName);

        return new ResponseEntity<>("Image deleted successfully", HttpStatus.ACCEPTED);
    }

    /**
     * PUT Method that renames a specific image.
     * Users with role USER can rename only their own images.
     * Users with role ADMIN can rename any images.
     *
     * @param userId         Long id of the user of the image
     * @param galleryName    String name of the gallery
     * @param imageName      String name of the image
     * @param newImageName   String new name for the image
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{userId}/{galleryName}/{imageName}")
    public ResponseEntity<String> renameImage(@PathVariable("userId") Long userId,
                                              @PathVariable("galleryName") String galleryName,
                                              @PathVariable("imageName") String imageName,
                                              @RequestParam String newImageName,
                                              @CurrentSecurityContext(expression = "authentication")
                                                      IdUsernamePasswordAuthenticationToken authentication) {

        imageService.renameImage(userId, galleryName, imageName, newImageName);

        return new ResponseEntity<>("Image renamed successfully", HttpStatus.ACCEPTED);
    }

}
