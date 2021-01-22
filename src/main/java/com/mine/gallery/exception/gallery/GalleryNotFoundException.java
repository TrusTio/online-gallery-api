package com.mine.gallery.exception.gallery;

/**
 * Exception used when a gallery is not found.
 *
 * @author TrusTio
 */
public class GalleryNotFoundException extends RuntimeException {
    public GalleryNotFoundException(String message) {
        super(message);
    }
}
