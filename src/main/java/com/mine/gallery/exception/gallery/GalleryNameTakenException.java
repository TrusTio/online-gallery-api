package com.mine.gallery.exception.gallery;

public class GalleryNameTakenException extends RuntimeException {
    public GalleryNameTakenException(String name) {
        super(String.format("Gallery with name %s exists!", name));
    }
}
