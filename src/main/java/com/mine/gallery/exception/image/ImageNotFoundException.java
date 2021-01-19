package com.mine.gallery.exception.image;

/**
 * Exception used when image is not found.
 *
 * @author TrusTio
 */
public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}
