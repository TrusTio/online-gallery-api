package com.mine.gallery.service;

import com.mine.gallery.exception.gallery.GalleryNotFoundException;
import com.mine.gallery.exception.image.ImageNotFoundException;
import com.mine.gallery.exception.image.ImageValidationException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.Image;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.ImageRepository;
import com.mine.gallery.persistence.repository.ImageStorageRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class used by {@link com.mine.gallery.controller.ImageController}.
 *
 * @author TrusTio
 */
@Service
public class ImageService {
    @Autowired
    private ImageStorageRepository imageStorageRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private GalleryRepository galleryRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Validates the {@link MultipartFile MultipartFile} then saves the file
     * to the local storage and saves information about it in the database.
     *
     * @param image     MultipartFile file to be saved
     * @param galleryId Long id of the gallery
     * @param userId    Long user id of the user
     * @return
     */
    public Long save(MultipartFile image, Long galleryId, Long userId) {
        Gallery gallery = galleryRepository.findByIdAndUserId(galleryId, userId)
                .orElseThrow(() -> new GalleryNotFoundException(galleryId));

        if (getImage(userId, galleryId, image.getOriginalFilename()).isPresent()) {
            throw new ImageValidationException("Image with that name already exists.");
        }

        String imageName = String.format("%s-%s", new Date().getTime(), image.getOriginalFilename());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("/")
                .append(userId).append("/")
                .append(galleryId).append("/")
                .append(imageName);

        String imageLocation = stringBuilder.toString();

        isValidImage(image);

        try {
            imageStorageRepository.saveImage(image.getBytes(), imageLocation);
        } catch (IOException e) {
            Logger.getLogger(ImageService.class.getName()).warning(e.getMessage());
        }

        return imageRepository.save(new Image()
                .setName(imageName)
                .setGallery(gallery)
                .setLocation(imageLocation))
                .getId();
    }

    /**
     * Fetches the requested image.
     * <p>
     * Throws {@link ImageValidationException} if it's not found.
     *
     * @param userId    Long id of hte user
     * @param galleryId Long id of the gallery
     * @param imageName String name of the image
     * @return FileSystemResource
     */
    public FileSystemResource find(Long userId, Long galleryId, String imageName) {
        Image image = getImage(userId, galleryId, imageName)
                .orElseThrow(() -> new ImageNotFoundException(imageName));

        return imageStorageRepository.findInFileSystem(image.getLocation());
    }

    /**
     * Deletes an image.
     *
     * @param userId    Long id of the user
     * @param galleryId Long id of the gallery
     * @param imageName String name of the image
     */
    public void deleteImage(Long userId, Long galleryId, String imageName) {
        Image image = getImage(userId, galleryId, imageName)
                .orElseThrow(() -> new ImageNotFoundException(imageName));

        imageStorageRepository.deleteImage(image.getLocation());
        imageRepository.delete(image);
    }

    /**
     * Renames the image.
     *
     * @param userId       Long id of the user
     * @param galleryId    String name of the gallery
     * @param imageName    String name of the image
     * @param newImageName String new name for the image
     */
    public void renameImage(Long userId, Long galleryId, String imageName, String newImageName) {

        Image image = getImage(userId, galleryId, imageName)
                .orElseThrow(() -> new ImageNotFoundException(imageName));

        newImageName = imageStorageRepository.renameImage(image.getLocation(), newImageName);
        image.setName(newImageName);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/")
                .append(userId).append("/")
                .append(galleryId).append("/")
                .append(newImageName);

        image.setLocation(stringBuilder.toString());

        imageRepository.save(image);
    }

    /**
     * Checks whether the image is valid by checking
     * if it's empty, bigger than 8 mb, not jpg/png
     * <p>
     * Throws {@link ImageValidationException}
     * if validation fails.
     *
     * @param image MultipartFile image to be checked
     * @return true if the image is valid
     */
    private boolean isValidImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new ImageValidationException("Image shouldn't be empty(null).");
        }
        if (image.getSize() > 8_000_000) {
            throw new ImageValidationException("Image size should not be bigger than 8 Mb.");
        }
        if (!(image.getContentType().equals("image/jpeg") ||
                image.getContentType().equals("image/png"))) {
            throw new ImageValidationException("The file should be a valid image with jpg/png extension.");
        }
        return true;
    }

    /**
     * Returns the {@link Image} object based on user id, gallery name and image name
     *
     * @param galleryId Long id of the gallery
     * @param imageName String name of the image
     * @return Optional<Image>
     */
    private Optional<Image> getImage(Long userId, Long galleryId, String imageName) {
        return imageRepository.findByNameAndGalleryId(imageName,
                galleryRepository.findByIdAndUserId(galleryId, userId)
                        .orElseThrow(() -> new GalleryNotFoundException(galleryId))
                        .getId());
    }
}
