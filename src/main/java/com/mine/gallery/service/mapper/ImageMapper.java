package com.mine.gallery.service.mapper;

import com.mine.gallery.persistence.entity.Image;
import com.mine.gallery.service.dto.ImageDTO;
import org.springframework.stereotype.Component;

/**
 * Maps the {@link Image Image} object to a {@link ImageDTO ImageDTO} object
 *
 * @author TrusTio
 */
@Component
public class ImageMapper {

    /**
     * Returns new {@link ImageDTO ImageDTO} object created from the {@link Image Image} parameter
     *
     * @param image Image object to be mapped to ImageDTO object
     * @return ImageDTO object with name and url
     */
    public static ImageDTO toImageDTO(Image image) {
        return new ImageDTO()
                .setName(image.getName())
                .setUrl("http://localhost:8080/api/v1/image" + image.getLocation());
    }
}
