package com.mine.gallery.service;

import com.mine.gallery.persistence.entity.Gallery;
import com.mine.gallery.persistence.entity.Image;
import com.mine.gallery.persistence.repository.FileSystemRepository;
import com.mine.gallery.persistence.repository.GalleryRepository;
import com.mine.gallery.persistence.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//TODO: Documentation
@Service
public class FileLocationService {
    @Autowired
    FileSystemRepository fileSystemRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    GalleryRepository galleryRepository;

    public Long save(byte[] bytes, String imageName, String galleryName, Long userId) throws Exception {

        Gallery gallery = galleryRepository.findByNameAndUserId(galleryName, userId).get();

        String location = fileSystemRepository.save(
                bytes,
                gallery.getUser().getId(),
                gallery.getName(),
                imageName);

        return imageRepository.save(new Image()
                .setName(imageName)
                .setGallery(gallery)
                .setLocation(location))
                .getId();
    }

    public FileSystemResource find(Long userId, String galleryName, String imageName) {

        Image image = imageRepository.findByNameAndGalleryId(imageName,
                galleryRepository.findByNameAndUserId(galleryName, userId).get().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found 1"));

        return fileSystemRepository.findInFileSystem(image.getLocation());
    }

}
