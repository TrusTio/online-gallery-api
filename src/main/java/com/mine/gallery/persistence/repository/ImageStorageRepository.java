package com.mine.gallery.persistence.repository;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Repository for the image storage.
 *
 * @author TrusTio
 */
@Repository
public class ImageStorageRepository {

    /**
     * Saves the image to local directory for using the userId and gallery name.
     *
     * @param content     byte[] content of the file to be saved
     * @param userId      Long userId of the user
     * @param galleryName String name of the gallery
     * @param imageName   String name of the iamge
     * @return
     * @throws Exception
     */
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

    /**
     * Finds the file in the system using the String path
     *
     * @param location String location of the file
     * @return
     */
    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception e) {
            // Handle access or file not found problems.
            throw new RuntimeException();
        }
    }

    /**
     * Gets the local image storage path.
     *
     * @return String containing the image storage path
     */
    private String getStoragePath() {
        StringBuilder stringBuilder = new StringBuilder();
        final String PROJECT_DIRECTORY = System.getProperty("user.dir");
        //TODO: Can this be extracted elsewhere
        return stringBuilder.append(PROJECT_DIRECTORY).append("/image-storage").toString();
    }

}
