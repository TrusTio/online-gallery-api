package com.mine.gallery.exception.image;

/**
 * Exception used for image validation issues.
 *
 * @author TrusTio
 */
public class ImageValidationException extends RuntimeException {
    public ImageValidationException(String message) {
        super(message);
    }
}
