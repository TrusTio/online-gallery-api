package com.mine.gallery.controller;

import com.mine.gallery.exception.gallery.CreateGalleryValidationException;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.persistence.repository.RoleRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.GalleryService;
import com.mine.gallery.service.dto.GalleryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        throw new CreateGalleryValidationException("You can't create a gallery in another profile.");
    }
}
