package com.mine.gallery.persistence.repository;

import com.mine.gallery.exception.gallery.GalleryValidationException;
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
     * @return String with absolute path to the uploaded file
     */
    public String saveImage(byte[] content, String location) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getStoragePath()).append("/")
                .append(location);
        String imageLocation = stringBuilder.toString();

        Path newFile = Paths.get(imageLocation);
        try {
            Files.createDirectories(newFile.getParent());

            Files.write(newFile, content);
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().toString());
        }

        return newFile.toAbsolutePath()
                .toString();
    }

    /**
     * Creates a gallery folder on the local directory using location
     *
     * @param userId      Long user Id of the owner of the folder
     * @param galleryName String name of the folder
     * @return String with absolute path to the created folder
     */
    public String saveGallery(Long userId, String galleryName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryName);
        String galleryLocation = stringBuilder.toString();

        Path newFolder = Paths.get(galleryLocation);
        try {
            Files.createDirectories(newFolder);
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().toString());
        }

        return newFolder.toAbsolutePath()
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
            throw new RuntimeException(e.getClass().toString());
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
            throw new RuntimeException(e.getClass().toString());
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
            throw new RuntimeException(e.getClass().toString());
        }
    }

    /**
     * Renames a gallery on the local storage.
     *
     * @param userId         Long id of the user owning the gallery
     * @param galleryName    String name of the gallery
     * @param newGalleryName String new name for the gallery
     */
    public void renameGallery(Long userId, String galleryName, String newGalleryName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryName);

        try {
            Path source = Paths.get(stringBuilder.toString());

            Files.move(source, source.resolveSibling(newGalleryName));
        } catch (FileAlreadyExistsException e) {
            throw new GalleryValidationException("Gallery with that name already exists.");
        } catch (InvalidPathException e) {
            throw new GalleryValidationException("Invalid gallery name.");
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().toString());
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
