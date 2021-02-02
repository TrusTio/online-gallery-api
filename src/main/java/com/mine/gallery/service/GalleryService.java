package com.mine.gallery.service;

import com.mine.gallery.exception.gallery.GalleryNotFoundException;
import com.mine.gallery.exception.gallery.GalleryValidationException;
import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.Image;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.ImageRepository;
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
    @Autowired
    private ImageRepository imageRepository;

    /**
     * Checks if the gallery name is valid. Then creates a folder for it and a new {@link Gallery Gallery}
     * and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.GalleryRepository GalleryRepository}
     *
     * @param galleryDTO The GalleryDTO object to be added as Gallery in the database
     * @return The GalleryDTO object saved in the database as Gallery
     */
    public GalleryDTO create(GalleryDTO galleryDTO, Errors errors) {
        if (galleryRepository.findByNameAndUserId(galleryDTO.getName(), galleryDTO.getUserId()).isPresent()) {
            throw new GalleryValidationException("Duplicate gallery name.");
        }

        if (errors.hasErrors()) {
            String exceptionMessage = ExceptionStringUtil.exceptionMessageBuilder(errors);
            throw new GalleryValidationException(exceptionMessage);
        }

        Gallery gallery = new Gallery()
                .setName(galleryDTO.getName())
                .setUser(userRepository.findById(galleryDTO.getUserId()).get());

        imageStorageRepository.saveGallery(gallery.getUser().getId(), gallery.getName());

        return GalleryMapper.toGalleryDTO(galleryRepository.save(gallery));
    }

    /**
     * Deletes a gallery and it's contents.
     *
     * @param id          Long id used to find the gallery
     * @param galleryName String name of the gallery to be deleted
     */
    public void delete(Long id, String galleryName) {
        Gallery gallery = galleryRepository.findByNameAndUserId(galleryName, id).get();
        imageStorageRepository.deleteGallery(id, galleryName);
        galleryRepository.delete(gallery);
    }

    /**
     * Renames a gallery.
     * Checks if the given gallery exists, if it does, attempts to rename the gallery on the local storage.
     * Then updates the gallery name in the database, after that changes the location of the
     * images in the database to their new location.
     *
     * @param id             Long id of the user
     * @param galleryName    String name of the gallery
     * @param newGalleryName String new gallery name
     */
    public void rename(Long id, String galleryName, String newGalleryName) {
        Gallery gallery = galleryRepository.findByNameAndUserId(galleryName, id)
                .orElseThrow(() -> new GalleryNotFoundException(
                        String.format("Gallery with name '%s' was not found.", galleryName)));

        imageStorageRepository.renameGallery(
                id,
                galleryName,
                newGalleryName);

        StringBuilder updatedImageLocation = new StringBuilder();
        updatedImageLocation.append("/")
                .append(id).append("/")
                .append(newGalleryName).append("/");

        gallery.setName(newGalleryName);
        galleryRepository.save(gallery);

        Iterable<Image> iterable = imageRepository.findAllByGalleryId(gallery.getId());
        iterable.forEach(image -> image.setLocation(updatedImageLocation.toString() + image.getName()));

        imageRepository.saveAll(iterable);
    }
}
