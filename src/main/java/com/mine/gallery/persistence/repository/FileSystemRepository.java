package com.mine.gallery.persistence.repository;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public class FileSystemRepository {
    public String save(byte[] content, Long userId, String galleryName, String imageName) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryName).append("/")
                .append(imageName);
        String imageLocation = stringBuilder.toString();

        Path newFile = Paths.get(imageLocation);
        Files.createDirectories(newFile.getParent());

        Files.write(newFile, content);

        return newFile.toAbsolutePath()
                .toString();
    }

    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception e) {
            // Handle access or file not found problems.
            throw new RuntimeException();
        }
    }

    private String getStoragePath() {
        StringBuilder stringBuilder = new StringBuilder();
        final String PROJECT_DIRECTORY = System.getProperty("user.dir");
        return stringBuilder.append(PROJECT_DIRECTORY).append("/image-storage").toString();
    }

}
