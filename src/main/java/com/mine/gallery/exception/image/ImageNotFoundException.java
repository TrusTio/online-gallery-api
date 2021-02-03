package com.mine.gallery.exception.image;

/**
 * Exception used when image is not found.
 *
 * @author TrusTio
 */
public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String imageName) {
        super(String.format("Image with name '%s' was not found!", imageName));
    }
}
