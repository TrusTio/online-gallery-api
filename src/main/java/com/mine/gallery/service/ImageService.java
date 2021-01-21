package com.mine.gallery.service;

import com.mine.gallery.exception.image.ImageNotFoundException;
import com.mine.gallery.exception.image.ImageValidationException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.Image;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.ImageRepository;
import com.mine.gallery.persistence.repository.ImageStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Optional;

/**
 * Service class used by {@link com.mine.gallery.controller.ImageController ImageController}.
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

    /**
     * Validates the {@link MultipartFile MultipartFile} then saves the file
     * to the local storage and saves information about it in the database.
     *
     * @param image       MultipartFile file to be saved
     * @param galleryName String name of the gallery
     * @param userId      Long id of the user
     * @return
     * @throws Exception
     */
    public Long save(MultipartFile image, String galleryName, Long userId) throws Exception {
        Gallery gallery = galleryRepository.findByNameAndUserId(galleryName, userId).get();

        if (getImage(userId, galleryName, image.getOriginalFilename()).isPresent()) {
            throw new ImageValidationException("Image with that name already exists.");
        }

        String imageName = String.format("%s-%s", new Date().getTime(), image.getOriginalFilename());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("/")
                .append(userId).append("/")
                .append(galleryName).append("/")
                .append(imageName);

        String imageLocation = stringBuilder.toString();

        isValidImage(image);

        imageStorageRepository.save(image.getBytes(), imageLocation);

        return imageRepository.save(new Image()
                .setName(imageName)
                .setGallery(gallery)
                .setLocation(imageLocation))
                .getId();
    }

    /**
     * Fetches the requested image.
     * <p>
     * Throws {@link ImageValidationException ImageValidationException} if it's not found.
     *
     * @param userId      Long id of the user
     * @param galleryName String name of the gallery
     * @param imageName   String name of the image
     * @return FileSystemResource
     */
    public FileSystemResource find(Long userId, String galleryName, String imageName) {

        Image image = getImage(userId, galleryName, imageName)
                .orElseThrow(() -> new ImageNotFoundException("No image found."));

        return imageStorageRepository.findInFileSystem(image.getLocation());
    }

    /**
     * Deletes an image.
     *
     * @param userId      Long id of the user
     * @param galleryName String name of the gallery
     * @param imageName   String name of the image
     */
    public void deleteImage(Long userId, String galleryName, String imageName) {
        Image image = getImage(userId, galleryName, imageName)
                .orElseThrow(() -> new ImageNotFoundException("No image found."));

        imageStorageRepository.deleteImage(image.getLocation());
        imageRepository.delete(image);
    }

    /**
     * Checks whether the image is valid by checking
     * if it's empty, bigger than 8 mb, not jpg/png
     * <p>
     * Throws {@link ImageValidationException ImageValidationException}
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
     * Returns the {@link Image Image} object based on user id, gallery name and image name
     *
     * @param userId      Long user id
     * @param galleryName String name of the gallery
     * @param imageName   String name of the image
     * @return Optional<Image>
     */
    private Optional<Image> getImage(Long userId, String galleryName, String imageName) {
        return imageRepository.findByNameAndGalleryId(imageName,
                galleryRepository.findByNameAndUserId(galleryName, userId)
                        .get().getId());
    }
}
