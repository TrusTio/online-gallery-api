package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.service.dto.GalleryContentsDTO;
import com.mine.gallery.service.dto.GalleryDTO;
import com.mine.gallery.service.dto.UserGalleriesDTO;

import java.util.stream.Collectors;

/**
 * Maps the {@link Gallery} object to a {@link GalleryDTO} object
 *
 * @author TrusTio
 */
public class GalleryMapper {
    /**
     * Returns new {@link GalleryDTO} object created from the {@link Gallery} parameter
     *
     * @param gallery {@link Gallery} object to be mapped to {@link GalleryDTO} object
     * @return {@link GalleryDTO} object with name and userId
     */
    public static GalleryDTO toGalleryDTO(Gallery gallery) {
        return new GalleryDTO()
                .setName(gallery.getName())
                .setUserId(gallery.getUser().getId());
    }

    /**
     * Returns new {@link UserGalleriesDTO} object created from the {@link Gallery} parameter
     *
     * @param gallery {@link Gallery} object to be mapped to {@link UserGalleriesDTO} object
     * @return {@link UserGalleriesDTO} object with id, name and url to contents
     */
    public static UserGalleriesDTO toUserGalleriesDTO(Gallery gallery) {
        return new UserGalleriesDTO()
                .setId(gallery.getId())
                .setName(gallery.getName())
                .setUrl("http://localhost:8080/api/v1/user/"
                        + gallery.getUser().getId()
                        + "/gallery/"
                        + gallery.getId());
    }

    /**
     * Returns new {@link GalleryContentsDTO} object created from the {@link Gallery} parameter
     *
     * @param gallery {@link Gallery} object to be mapped to {@link GalleryContentsDTO} object
     * @return {@link GalleryContentsDTO} object with with id, name and image urls
     */
    public static GalleryContentsDTO toGalleryContentsDTO(Gallery gallery) {
        return new GalleryContentsDTO()
                .setId(gallery.getId())
                .setName(gallery.getName())
                .setImages(gallery.getImages()
                        .stream().map(ImageMapper::toImageDTO)
                        .collect(Collectors.toList()));
    }
}
