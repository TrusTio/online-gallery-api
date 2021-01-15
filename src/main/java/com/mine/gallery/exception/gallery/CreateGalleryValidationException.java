package com.mine.gallery.exception.gallery;

/**
 * Exception used for when the gallery creation validation of fails.
 *
 * @author TrusTio
 */
public class CreateGalleryValidationException extends RuntimeException {
    public CreateGalleryValidationException(String message) {
        super(message);
    }
}
