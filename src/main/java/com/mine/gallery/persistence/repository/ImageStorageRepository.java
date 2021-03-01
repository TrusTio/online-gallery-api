package com.mine.gallery.persistence.repository;

import com.mine.gallery.exception.gallery.GalleryValidationException;
import com.mine.gallery.exception.image.ImageValidationException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;

import java.io.File;
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

            String absolutePath = newFile.toAbsolutePath().toString();
            Thumbnails.of(new File(absolutePath))
                    .size(250, 140)
                    .toFiles(Rename.PREFIX_DOT_THUMBNAIL);

        } catch (Exception e) {
            throw new RuntimeException(e.getClass().toString());
        }

        return newFile.toAbsolutePath()
                .toString();
    }

    /**
     * Creates a gallery folder on the local directory using location
     *
     * @param userId    Long user Id of the owner of the folder
     * @param galleryId Long id of the folder
     * @return String with absolute path to the created folder
     */
    public String saveGallery(Long userId, Long galleryId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryId);
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
     * @return FileSystemResource
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
     * @param userId    Long id of the user
     * @param galleryId Long id of the gallery
     * @param imageName String name of image
     */
    public void deleteImage(Long userId, Long galleryId, String imageName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryId).append("/");
        String imageLocation = stringBuilder.toString() + imageName;
        String thumbnailLocation = stringBuilder.toString() + "thumbnail." + imageName;

        try {
            Files.delete(Paths.get(imageLocation));
            Files.delete(Paths.get(thumbnailLocation));
        } catch (Exception e) {
            throw new RuntimeException(e.getClass().toString());
        }
    }

    /**
     * Rename image on local storage.
     *
     * @param userId       Long id of the user
     * @param galleryId    Long id of the gallery
     * @param imageName    String name of image
     * @param newImageName String new image name
     * @return String new name of the image with the extension
     */
    public String renameImage(Long userId, Long galleryId, String imageName, String newImageName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryId).append("/");
        String imageLocation = stringBuilder.toString() + imageName;
        String thumbnailLocation = stringBuilder.toString() + "thumbnail." + imageName;

        try {
            // image
            Path source = Paths.get(imageLocation);
            String extension = FilenameUtils.getExtension(imageLocation);
            String nameWithExtension = newImageName + "." + extension;

            Files.move(source, source.resolveSibling(nameWithExtension));

            //thumbnail
            Path thumbnailSource = Paths.get(thumbnailLocation);
            String thumbnailExtension = FilenameUtils.getExtension(thumbnailLocation);
            String thumbnailNameWithExtension = "thumbnail." + newImageName + "." + thumbnailExtension;

            Files.move(thumbnailSource, thumbnailSource.resolveSibling(thumbnailNameWithExtension));

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
     * @param userId    Long id of the user owning the gallery
     * @param galleryId Long id of the gallery
     */
    public void deleteGallery(Long userId, Long galleryId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStoragePath()).append("/")
                .append(userId).append("/")
                .append(galleryId);

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
        return stringBuilder.append(PROJECT_DIRECTORY).append("/image-storage").toString();
    }

}
