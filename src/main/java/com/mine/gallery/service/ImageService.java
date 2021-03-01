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
import com.mine.gallery.service.dto.ImageDTO;
import com.mine.gallery.service.mapper.ImageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Image related methods.
 *
 * @author TrusTio
 */
@Service
@Slf4j
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
    @Transactional
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
            log.error(e.getMessage());
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
    public FileSystemResource findImage(Long userId, Long galleryId, String imageName) {
        Image image = getImage(userId, galleryId, imageName)
                .orElseThrow(() -> new ImageNotFoundException(imageName));

        return imageStorageRepository.findInFileSystem(image.getLocation());
    }

    /**
     * Fetches the requested image thumbnail.
     * <p>
     * Throws {@link ImageValidationException} if it's not found.
     *
     * @param userId    Long id of hte user
     * @param galleryId Long id of the gallery
     * @param imageName String name of the image
     * @return FileSystemResource
     */
    public FileSystemResource findImageThumbnail(Long userId, Long galleryId, String imageName) {
        Image image = getImage(userId, galleryId, imageName)
                .orElseThrow(() -> new ImageNotFoundException(imageName));

        if(image !=null){
            return imageStorageRepository.findImageThumbnail(userId, galleryId, imageName);
        }
        throw new ImageNotFoundException(imageName);
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

        imageStorageRepository.deleteImage(userId, galleryId, imageName);
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

        newImageName = imageStorageRepository.renameImage(userId, galleryId, imageName, newImageName);
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
     * Fetches a list of the images(id, name and url) the user has in specific gallery
     *
     * @param pageNo    Integer Number of the page to be fetched
     * @param pageSize  Integer Size of the pages
     * @param sortBy    String sort by field
     * @param userId    Long id of the user to be fetched
     * @param galleryId Long id of the gallery
     * @return {@link List<ImageDTO>}
     */
    public List<ImageDTO> getUserGalleryImages(Integer pageNo, Integer pageSize, String sortBy,
                                               Long userId, Long galleryId) {
        Gallery gallery = galleryRepository.findByIdAndUserId(galleryId, userId)
                .orElseThrow(() -> new GalleryNotFoundException(galleryId));

        return imageRepository.findAllByGalleryId(gallery.getId(), PageRequest.of(pageNo, pageSize, Sort.by(sortBy)))
                .stream().map(ImageMapper::toImageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a list of the images(id, name and url) the user has in all galleries.
     *
     * @param pageNo   Integer Number of the page to be fetched
     * @param pageSize Integer Size of the pages
     * @param sortBy   String sort by field
     * @param userId   Long id of the user to be fetched
     * @return {@link List<ImageDTO>}
     */
    public List<ImageDTO> getUserImages(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "20") Integer pageSize,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @PathVariable("userId") Long userId) {
        List<Gallery> userGalleries = galleryRepository.findAllByUserId(userId);
        Long[] galleryIds = userGalleries
                .stream().map(Gallery::getId).toArray(Long[]::new);

        return imageRepository.findAllByGalleryIdIn(galleryIds, PageRequest.of(pageNo, pageSize, Sort.by(sortBy)))
                .stream().map(ImageMapper::toImageDTO)
                .collect(Collectors.toList());
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
