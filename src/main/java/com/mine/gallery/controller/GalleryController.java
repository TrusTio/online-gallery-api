package com.mine.gallery.controller;

import com.mine.gallery.security.IdUsernamePasswordAuthenticationToken;
import com.mine.gallery.service.GalleryService;
import com.mine.gallery.service.dto.GalleryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.logging.Logger;

/**
 * Gallery controller that exposes gallery end points
 *
 * @author TrusTio
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/galleries")
public class GalleryController {
    @Autowired
    private GalleryService galleryService;

    /**
     * A POST method that accepts {@link GalleryDTO} body with it's parameters to create a new gallery in the database
     * using the create service from {@link com.mine.gallery.service.GalleryService}
     * Users with role USER can only create galleries for their own accounts.
     * Users with role ADMIN can create galleries in any account.
     *
     * @param galleryDTO     {@link GalleryDTO} object/body used to create new gallery
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String> confirming the creation
     */
    @PreAuthorize("#galleryDTO.userId == #authentication.id  || hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody GalleryDTO galleryDTO,
                                         Errors errors,
                                         @CurrentSecurityContext(expression = "authentication")
                                                 IdUsernamePasswordAuthenticationToken authentication) {

        galleryService.create(galleryDTO, errors);
        Logger.getLogger(UserController.class.getName()).info("Created new gallery!");

        return new ResponseEntity<>("Gallery created successfully", HttpStatus.CREATED);
    }

    /**
     * DELETE Method that deletes a gallery of a specific user and the contents it has.
     * Users with role USER can only delete galleries for their own accounts.
     * Users with role ADMIN can delete galleries in any account.
     *
     * @param userId         Long id of the gallery owner
     * @param galleryId      Long id of the gallery to be deleted
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{userId}/{galleryId}")
    public ResponseEntity<String> delete(@PathVariable("userId") Long userId,
                                         @PathVariable("galleryId") Long galleryId,
                                         @CurrentSecurityContext(expression = "authentication")
                                                 IdUsernamePasswordAuthenticationToken authentication) {

        galleryService.delete(userId, galleryId);

        return new ResponseEntity<>("Gallery deleted successfully", HttpStatus.ACCEPTED);
    }

    /**
     * PATCH Method that renames the given gallery.
     * Users with role USER can only rename galleries for their own accounts.
     * Users with role ADMIN can rename galleries in any account.
     *
     * @param userId         Long id of the gallery owner
     * @param galleryId      Long id of the gallery to be deleted
     * @param newGalleryName String new gallery name
     * @param authentication {@link IdUsernamePasswordAuthenticationToken} holds data for the current user
     * @return ResponseEntity<String>
     */
    @PreAuthorize("#userId == #authentication.id || hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}/{galleryId}")
    public ResponseEntity<String> rename(@PathVariable("userId") Long userId,
                                         @PathVariable("galleryId") Long galleryId,
                                         @RequestParam String newGalleryName,
                                         @CurrentSecurityContext(expression = "authentication")
                                                 IdUsernamePasswordAuthenticationToken authentication) {

        galleryService.rename(userId, galleryId, newGalleryName);

        return new ResponseEntity<>("Gallery updated successfully", HttpStatus.ACCEPTED);
    }
}
