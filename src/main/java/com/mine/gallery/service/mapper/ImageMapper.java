package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.Image;
import com.mine.gallery.service.dto.ImageDTO;
import org.springframework.stereotype.Component;

/**
 * Maps the {@link Image} object to a {@link ImageDTO} object
 *
 * @author TrusTio
 */
@Component
public class ImageMapper {

    /**
     * Returns new {@link ImageDTO} object created from the {@link Image} parameter
     *
     * @param image {@link Image} object to be mapped to ImageDTO object
     * @return {@link ImageDTO} object with name and url
     */
    public static ImageDTO toImageDTO(Image image) {
        return new ImageDTO()
                .setId(image.getId())
                .setName(image.getName())
                .setThumbnail("http://localhost:8080/api/v1/image"
                        + "/" + image.getGallery().getUser().getId()
                        + "/" + image.getGallery().getId()
                        + "/" + image.getName()
                        + "/thumbnail")
                .setUrl("http://localhost:8080/api/v1/image"
                        + "/" + image.getGallery().getUser().getId()
                        + "/" + image.getGallery().getId()
                        + "/" + image.getName());
    }
}
