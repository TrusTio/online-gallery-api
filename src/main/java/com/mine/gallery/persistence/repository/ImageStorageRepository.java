package com.mine.gallery.persistence.repository;

import com.mine.gallery.exception.image.ImageValidationException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
     * @param content byte[] content of the file to be saved
     * @return
     * @throws Exception
     */
    public String save(byte[] content, String location) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getStoragePath()).append("/")
                .append(location);
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(location);
        try {
            return new FileSystemResource(Paths.get(stringBuilder.toString()));
        } catch (Exception e) {
            // Handle access or file not found problems.
            throw new RuntimeException();
        }
    }

    /**
     * Deletes the image in the specified location.
     *
     * @param location String location of the image to be deleted
     */
    public void deleteImage(String location) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(location);

        try {
            Files.delete(Paths.get(stringBuilder.toString()));
        } catch (Exception e) {
            // Handle access or file not found problems.
            throw new RuntimeException();
        }
    }

    /**
     * Rename image on local storage.
     *
     * @param location     String location of the image to be renamed
     * @param newImageName String new image name
     * @return new name of the image with the extension
     */
    public String renameImage(String location, String newImageName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(location);

        try {
            Path source = Paths.get(stringBuilder.toString());
            String extension = FilenameUtils.getExtension(stringBuilder.toString());
            String nameWithExtension = newImageName + "." + extension;

            Files.move(source, source.resolveSibling(nameWithExtension));
            
            return nameWithExtension;
        } catch (FileAlreadyExistsException e) {
            throw new ImageValidationException("Image with that name already exists.");
        } catch (InvalidPathException e) {
            throw new ImageValidationException("Invalid image name.");
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().toString());
        }
    }

    /**
     * Deletes the gallery at the specified location and it's contents
     *
     * @param userId      Long id of the user owning the gallery
     * @param galleryName String name of the gallery
     */
    public void deleteGallery(Long userId, String galleryName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryName);
        try {
            FileSystemUtils.deleteRecursively(Paths.get(stringBuilder.toString()));
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
