package com.mine.gallery.exception.gallery;

/**
 * Exception used when a gallery is not found.
 *
 * @author TrusTio
 */
public class GalleryNotFoundException extends RuntimeException {
    public GalleryNotFoundException(Long galleryId) {
        super(String.format("Gallery with id '%d' was not found.", galleryId));
    }
}
