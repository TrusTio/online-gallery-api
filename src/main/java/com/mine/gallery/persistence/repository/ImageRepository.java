package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the {@link Image} class/table
 *
 * @author TrusTio
 */
public interface ImageRepository extends JpaRepository<Image, Integer> {
    Optional<Image> findById(Long id);

    Optional<Image> findByNameAndGalleryId(String name, Long galleryId);

    Iterable<Image> findAllByGalleryId(Long galleryId);

    Page<Image> findAllByGalleryId(Long galleryId, Pageable pageable);

    Page<Image> findAllByGalleryIdIn(Long[] galleryId, Pageable pageable);

    Page<Image> findAllByGalleryIdInAndNameContaining(Long[] galleryId, String name, Pageable pageable);

    void delete(Image image);
}