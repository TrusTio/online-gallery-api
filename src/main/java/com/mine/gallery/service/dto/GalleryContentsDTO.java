package com.mine.gallery.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * DTO class for the {@link com.mine.gallery.persistence.entity.Gallery}
 *
 * @author TrusTio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GalleryContentsDTO {
    private Long id;
    private String name;
    private List<ImageDTO> images;
}
