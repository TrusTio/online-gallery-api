package com.mine.gallery.controller;

import com.mine.gallery.persistence.entity.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1/image")
public class ImageController {
    @Autowired
    private com.mine.gallery.persistence.repository.ImageRepository imageRepository;
    @Autowired
    private com.mine.gallery.persistence.repository.GalleryRepository galleryRepository;
    @Autowired
    private com.mine.gallery.service.FileLocationService fileLocationService;
    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;
    @Autowired
    private com.mine.gallery.persistence.repository.RoleRepository roleRepository;

    @PostMapping("/upload")
    public @ResponseBody
    String uploadImage(@RequestParam("imageFile") MultipartFile image,
                       @RequestParam("galleryName") String galleryName,
                       Principal principal
    ) throws Exception {

        fileLocationService.save(image.getBytes(),
                image.getOriginalFilename(),
                galleryName,
                userRepository.findByUsername(principal.getName()).getId());

        return "Image Saved";
    }

    @GetMapping(value = "/{userId}/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    FileSystemResource retrieveImage(@PathVariable Long userId,
                                     @PathVariable String galleryName,
                                     @PathVariable String imageName,
                                     Principal principal) throws Exception {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || userRepository.findById(userId).get().getUsername().equals(principal.getName())) {
            ;
            return fileLocationService.find(userId, galleryName, imageName);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

}
