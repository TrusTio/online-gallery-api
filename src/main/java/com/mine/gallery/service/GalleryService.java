package com.mine.gallery.service;

import com.mine.gallery.exception.gallery.GalleryNotFoundException;
import com.mine.gallery.exception.gallery.GalleryValidationException;
import com.mine.gallery.exception.user.UserNotFoundException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.ImageRepository;
import com.mine.gallery.persistence.repository.ImageStorageRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.dto.GalleryDTO;
import com.mine.gallery.service.dto.UserGalleriesDTO;
import com.mine.gallery.service.mapper.GalleryMapper;
import com.mine.gallery.util.ExceptionStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;


/**
 * Service class for the {@link com.mine.gallery.controller.GalleryController}
 *
 * @author TrusTio
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GalleryService {
    @Autowired
    private GalleryRepository galleryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageStorageRepository imageStorageRepository;
    @Autowired
    private ImageRepository imageRepository;

    /**
     * Checks if the gallery name is valid. Then creates a folder for it and a new {@link Gallery} object
     * to assign the values of the DTO to it, then adds it to the database.
     * using {@link GalleryRepository}
     *
     * @param galleryDTO The {@link GalleryDTO} object to be added as Gallery in the database
     * @return The {@link GalleryDTO} object saved as {@link Gallery} in the database as Gallery
     */
    @Transactional
    public UserGalleriesDTO create(GalleryDTO galleryDTO, Errors errors) {
        if (galleryRepository.findByNameAndUserId(galleryDTO.getName(), galleryDTO.getUserId()).isPresent()) {
            throw new GalleryValidationException("Duplicate gallery name.");
        }

        if (errors.hasErrors()) {
            String exceptionMessage = ExceptionStringUtil.exceptionMessageBuilder(errors);
            throw new GalleryValidationException(exceptionMessage);
        }

        Gallery gallery = new Gallery()
                .setName(galleryDTO.getName())
                .setUser(userRepository.findById(galleryDTO.getUserId())
                        .orElseThrow(() -> new UserNotFoundException(galleryDTO.getUserId())));

        gallery = galleryRepository.save(gallery);

        imageStorageRepository.saveGallery(gallery.getUser().getId(), gallery.getId());

        return GalleryMapper.toUserGalleriesDTO(gallery);
    }

    /**
     * Deletes a gallery and it's contents.
     *
     * @param userId    Long id of the user used to find the gallery
     * @param galleryId Long id of the gallery to be deleted
     */
    public void delete(Long userId, Long galleryId) {
        Gallery gallery = galleryRepository.findByIdAndUserId(galleryId, userId)
                .orElseThrow(() -> new GalleryNotFoundException(galleryId));

        imageStorageRepository.deleteGallery(userId, galleryId);

        galleryRepository.delete(gallery);
    }

    /**
     * Renames a gallery.
     * Checks if the given gallery exists, if it does, attempts to rename the gallery on the local storage.
     * Then updates the gallery name in the database, after that changes the location of the
     * images in the database to their new location.
     *
     * @param userId         Long id of the user
     * @param galleryId      Long id of the gallery of the gallery
     * @param newGalleryName String new gallery name
     */
    public void rename(Long userId, Long galleryId, String newGalleryName) {
        Gallery gallery = galleryRepository.findByIdAndUserId(galleryId, userId)
                .orElseThrow(() -> new GalleryNotFoundException(galleryId));

        gallery.setName(newGalleryName);

        galleryRepository.save(gallery);
    }
}
