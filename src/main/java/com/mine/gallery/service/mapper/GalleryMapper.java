package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.service.dto.GalleryDTO;

/**
 * Maps the {@link Gallery} object to a {@link GalleryDTO} object
 *
 * @author TrusTio
 */
public class GalleryMapper {
    /**
     * Returns new {@link GalleryDTO} object created from the {@link Gallery} parameter
     *
     * @param gallery {@link Gallery} object to be mapped to GalleryDTO object
     * @return {@link GalleryDTO} object with name and userId
     */
    public static GalleryDTO toGalleryDTO(Gallery gallery) {
        return new GalleryDTO()
                .setName(gallery.getName())
                .setUserId(gallery.getUser().getId());
    }
}
