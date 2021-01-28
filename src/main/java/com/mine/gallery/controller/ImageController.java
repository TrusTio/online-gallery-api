package com.mine.gallery.controller;

import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.security.Principal;


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
     * @param image       MultipartFile image to be uploaded
     * @param galleryName String name of the gallery
     * @param principal   Principal
     * @return String
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("imageFile") MultipartFile image,
                                              @RequestParam("galleryName") String galleryName,
                                              Principal principal) {
        imageService.save(image,
                galleryName,
                principal.getName());
        return new ResponseEntity<>("Image uploaded successfully", HttpStatus.CREATED);
    }

    /**
     * A GET method that fetches a specific image.
     * Users with role USER can access only their own images.
     * Users with role ADMIN can access all images.
     *
     * @param username    String the username of the image
     * @param galleryName String name of the gallery
     * @param imageName   String name of the image
     * @param principal   Principal
     * @return FileSystemResource
     */
    @PreAuthorize("#username == #principal.name || hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{username}/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public FileSystemResource retrieveImage(@PathVariable("username") String username,
                                            @PathVariable("galleryName") String galleryName,
                                            @PathVariable("imageName") String imageName,
                                            Principal principal) {

        return imageService.find(username, galleryName, imageName);
    }

    /**
     * DELETE method that deletes a specific image
     * Users with role USER can delete only their own images.
     * Users with role ADMIN can delete any images.
     *
     * @param username    String the username of the image
     * @param galleryName String name of the gallery
     * @param imageName   String name of the image
     * @param principal   Principal
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#username == #principal.name || hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{username}/{galleryName}/{imageName}")
    public ResponseEntity<String> deleteImage(@PathVariable("username") String username,
                                              @PathVariable("galleryName") String galleryName,
                                              @PathVariable("imageName") String imageName,
                                              Principal principal) {
        imageService.deleteImage(username, galleryName, imageName);
        return new ResponseEntity<>("Image deleted successfully", HttpStatus.ACCEPTED);
    }

    /**
     * PUT Method that renames a specific image.
     * Users with role USER can rename only their own images.
     * Users with role ADMIN can rename any images.
     *
     * @param username    String the username of the image
     * @param galleryName  String name of the gallery
     * @param imageName    String name of the image
     * @param newImageName String new name for the image
     * @param principal    Principal
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#username == #principal.name || hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{username}/{galleryName}/{imageName}")
    public ResponseEntity<String> renameImage(@PathVariable("username") String username,
                                              @PathVariable("galleryName") String galleryName,
                                              @PathVariable("imageName") String imageName,
                                              @RequestParam String newImageName,
                                              Principal principal) {

            imageService.renameImage(username, galleryName, imageName, newImageName);

            return new ResponseEntity<>("Image renamed successfully", HttpStatus.ACCEPTED);
    }

}
