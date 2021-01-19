package com.mine.gallery.controller;

import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.persistence.entity.RoleName;
import com.mine.gallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
    private ImageService imageService;
    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;
    @Autowired
    private com.mine.gallery.persistence.repository.RoleRepository roleRepository;

    //TODO: Add a way to set custom name to the image with optional param

    /**
     * A POST method that lets the user upload an image
     * to his own gallery.
     *
     * @param image       MultipartFile image to be uploaded
     * @param galleryName String name of the gallery
     * @param principal   Principal
     * @return String
     * @throws Exception
     */
    @PostMapping("/upload")
    public @ResponseBody
    String uploadImage(@RequestParam("imageFile") MultipartFile image,
                       @RequestParam("galleryName") String galleryName,
                       Principal principal
    ) throws Exception {
        imageService.save(image,
                galleryName,
                userRepository.findByUsername(principal.getName()).getId());
        return "Image Saved";
    }

    /**
     * A GET method that fetches a specific image.
     * Users with role USER can access only their own images.
     * Users with role ADMIN can access all images.
     *
     * @param userId      Long the user id of the image
     * @param galleryName String name of the gallery
     * @param imageName   String name of the image
     * @param principal   Principal
     * @return FileSystemResource
     * @throws Exception
     */
    @GetMapping(value = "/{userId}/{galleryName}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    FileSystemResource retrieveImage(@PathVariable Long userId,
                                     @PathVariable String galleryName,
                                     @PathVariable String imageName,
                                     Principal principal) throws Exception {
        if (userRepository.findByUsername(principal.getName()).getRoles()
                .contains(roleRepository.findByName(RoleName.ROLE_ADMIN).get())
                || userRepository.findById(userId).get().getUsername().equals(principal.getName())) {
            return imageService.find(userId, galleryName, imageName);
        } else {
            throw new UnauthorizedAccessException("Access denied, you can't check other users files.");
        }
    }

}
