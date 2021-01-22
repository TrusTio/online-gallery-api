package com.mine.gallery.controller;

import com.mine.gallery.exception.gallery.GalleryValidationException;
import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.GalleryService;
import com.mine.gallery.service.dto.GalleryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.logging.Logger;

/**
 * Gallery controller that exposes gallery end points
 *
 * @author TrusTio
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/gallery")
public class GalleryController {
    @Autowired
    private GalleryService galleryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    /**
     * A POST method that accepts {@link GalleryDTO GalleryDTO} body with it's parameters to create a new gallery in the database
     * using the create service from {@link com.mine.gallery.service.GalleryService GalleryService}
     * Users with role USER can only create galleries for their own accounts.
     * Users with role ADMIN can create galleries in any account.
     *
     * @param galleryDTO GalleryDTO object/body used to create new gallery
     * @return String confirming the creation
     */
    @PostMapping("/create")
    public String create(@Valid @RequestBody GalleryDTO galleryDTO, Errors errors, Principal principal) {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || userRepository.findByUsername(principal.getName()).getId().equals(galleryDTO.getUserId())) {

            if (galleryService.create(galleryDTO, errors) != null) {
                Logger.getLogger(UserController.class.getName()).info("Created new gallery!");
                return "Gallery created!";
            }
        }
        throw new GalleryValidationException("You can't create a gallery in another profile.");
    }

    /**
     * DELETE Method that deletes a gallery of a specific user and the contents it has.
     * Users with role USER can only delete galleries for their own accounts.
     * Users with role ADMIN can delete galleries in any account.
     *
     * @param username    String username of the gallery owner
     * @param galleryName String gallery name to be deleted
     * @param principal   Principal
     * @return
     */
    @DeleteMapping("/{username}/{galleryName}")
    public String delete(@PathVariable("username") String username,
                         @PathVariable("galleryName") String galleryName,
                         Principal principal) {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || username.equals(principal.getName())) {

            galleryService.delete(username, galleryName);

            return "Gallery deleted";
        }
        throw new UnauthorizedAccessException("Can't delete the gallery of another user");
    }

    @PutMapping("/{username}/{galleryName}")
    public String rename(@PathVariable("username") String username,
                         @PathVariable("galleryName") String galleryName,
                         @RequestParam String newGalleryName,
                         Principal principal) {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || username.equals(principal.getName())) {

            galleryService.rename(username, galleryName, newGalleryName);

            return "Gallery renamed";
        }
        throw new UnauthorizedAccessException("Can't rename the gallery of another user");
    }
}
