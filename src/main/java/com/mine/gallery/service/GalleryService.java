package com.mine.gallery.service;

import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.service.dto.GalleryDTO;
import com.mine.gallery.service.mapper.GalleryMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    private com.mine.gallery.persistence.repository.GalleryRepository galleryRepository;
    @Autowired
    private com.mine.gallery.persistence.repository.UserRepository userRepository;

    /**
     * Creates a new {@link Gallery Gallery} and assigns the values of the DTO to it, then adds it to the database
     * using {@link com.mine.gallery.persistence.repository.GalleryRepository GalleryRepository}
     *
     * @param galleryDTO The GalleryDTO object to be added as Gallery in the database
     * @return The GalleryDTO object saved in the database as Gallery
     */
    public GalleryDTO create(GalleryDTO galleryDTO) {
        if (galleryRepository.findByNameAndUserId(galleryDTO.getName(), galleryDTO.getUserId()).isPresent()) {
            return null;
        }
        Gallery gallery = new Gallery()
                .setName(galleryDTO.getName())
                .setUser(userRepository.findById(galleryDTO.getUserId()).get());

        return GalleryMapper.toGalleryDTO(galleryRepository.save(gallery));
    }
}
