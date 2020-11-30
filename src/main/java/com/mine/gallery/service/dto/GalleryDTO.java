package com.mine.gallery.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO class for the {@link com.mine.gallery.persistence.entity.Gallery Gallery}
 *
 * @author TrusTio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GalleryDTO {
    private String name;
    private Long userId;
}
