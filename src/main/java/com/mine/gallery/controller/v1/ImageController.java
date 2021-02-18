package com.mine.gallery.controller.v1;

import com.mine.gallery.security.IdUsernamePasswordAuthenticationToken;
import com.mine.gallery.service.ImageService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@Slf4j
public class ImageController {
    @Autowired
    private ImageService imageService;

    /**
     * A POST method that lets the user upload an image
     * to his own gallery.
     *
     * @param image          MultipartFile image to be uploaded
     * @param galleryId      Long id of the gallery
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@RequestParam("imageFile") MultipartFile image,
                                              @RequestParam("galleryId") Long galleryId,
                                              @CurrentSecurityContext(expression = "authentication")
                                                      IdUsernamePasswordAuthenticationToken authentication) {
        imageService.save(image, galleryId, authentication.getId());
        log.info("Image uploaded successfully!");

        return new ResponseEntity<>("Image uploaded successfully!", HttpStatus.CREATED);
    }

    /**
     * A GET method that fetches a specific image.
     * Users with role USER can access only their own images.
     * Users with role ADMIN can access all images.
     *
     * @param userId         Long id of the user of the image
     * @param galleryId      Long id of the gallery
     * @param imageName      String name of the image
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return FileSystemResource
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{userId}/{galleryId}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public FileSystemResource retrieveImage(@PathVariable("userId") Long userId,
                                            @PathVariable("galleryId") Long galleryId,
                                            @PathVariable("imageName") String imageName,
                                            @CurrentSecurityContext(expression = "authentication")
                                                    IdUsernamePasswordAuthenticationToken authentication) {
        log.info("Image fetched successfully!");

        return imageService.find(userId, galleryId, imageName);
    }

    /**
     * DELETE method that deletes a specific image
     * Users with role USER can delete only their own images.
     * Users with role ADMIN can delete any images.
     *
     * @param userId         Long id of the user of the image
     * @param galleryId      Long id of the gallery
     * @param imageName      String name of the image
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{userId}/{galleryId}/{imageName}")
    public ResponseEntity<String> deleteImage(@PathVariable("userId") Long userId,
                                              @PathVariable("galleryId") Long galleryId,
                                              @PathVariable("imageName") String imageName,
                                              @CurrentSecurityContext(expression = "authentication")
                                                      IdUsernamePasswordAuthenticationToken authentication) {
        imageService.deleteImage(userId, galleryId, imageName);
        log.info("Image deleted successfully!");

        return new ResponseEntity<>("Image deleted successfully!", HttpStatus.ACCEPTED);
    }

    /**
     * PATCH Method that renames a specific image.
     * Users with role USER can rename only their own images.
     * Users with role ADMIN can rename any images.
     *
     * @param userId         Long id of the user of the image
     * @param galleryId      Long id of the gallery
     * @param imageName      String name of the image
     * @param newImageName   String new name for the image
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/{userId}/{galleryId}/{imageName}")
    public ResponseEntity<String> renameImage(@PathVariable("userId") Long userId,
                                              @PathVariable("galleryId") Long galleryId,
                                              @PathVariable("imageName") String imageName,
                                              @RequestParam String newImageName,
                                              @CurrentSecurityContext(expression = "authentication")
                                                      IdUsernamePasswordAuthenticationToken authentication) {
        imageService.renameImage(userId, galleryId, imageName, newImageName);
        log.info("Image renamed successfully!");

        return new ResponseEntity<>("Image renamed successfully!", HttpStatus.ACCEPTED);
    }

}
