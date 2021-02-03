package com.mine.gallery.exception.gallery;

/**
 * Exception used when a gallery is not found.
 *
 * @author TrusTio
 */
public class GalleryNotFoundException extends RuntimeException {
    public GalleryNotFoundException(String galleryName) {
        super(String.format("Gallery with name '%s' was not found.", galleryName));
    }
}
