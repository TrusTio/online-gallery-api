package com.mine.gallery.service;

import com.mine.gallery.exception.gallery.CreateGalleryValidationException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.ImageStorageRepository;
import com.mine.gallery.persistence.repository.UserRepository;
import com.mine.gallery.service.dto.GalleryDTO;
import com.mine.gallery.service.mapper.GalleryMapper;
import com.mine.gallery.util.ExceptionStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;


/**
 * Service class for the {@link com.mine.gallery.controller.GalleryController GalleryController}
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

    /**
     * Checks if the gallery name is valid. Then creates a new {@link Gallery Gallery}
     * and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.GalleryRepository GalleryRepository}
     *
     * @param galleryDTO The GalleryDTO object to be added as Gallery in the database
     * @return The GalleryDTO object saved in the database as Gallery
     */
    public GalleryDTO create(GalleryDTO galleryDTO, Errors errors) {
        if (galleryRepository.findByNameAndUserId(galleryDTO.getName(), galleryDTO.getUserId()).isPresent()) {
            throw new CreateGalleryValidationException("Duplicate gallery name.");
        }

        if (errors.hasErrors()) {
            String exceptionMessage = ExceptionStringUtil.exceptionMessageBuilder(errors);
            throw new CreateGalleryValidationException(exceptionMessage);
        }

        Gallery gallery = new Gallery()
                .setName(galleryDTO.getName())
                .setUser(userRepository.findById(galleryDTO.getUserId()).get());

        return GalleryMapper.toGalleryDTO(galleryRepository.save(gallery));
    }

    /**
     * Deletes a gallery and it's contents.
     *
     * @param username    String username used to find the gallery
     * @param galleryName String name of the gallery to be deleted
     */
    public void delete(String username, String galleryName) {
        Gallery gallery = galleryRepository.findByNameAndUserId(galleryName, userRepository.findByUsername(username).getId()).get();
        imageStorageRepository.deleteGallery(userRepository.findByUsername(username).getId(), galleryName);
        galleryRepository.delete(gallery);
    }
}
