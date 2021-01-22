package com.mine.gallery.exception.gallery;

/**
 * Exception used for when the gallery creation validation of fails.
 *
 * @author TrusTio
 */
public class GalleryValidationException extends RuntimeException {
    public GalleryValidationException(String message) {
        super(message);
    }
}
