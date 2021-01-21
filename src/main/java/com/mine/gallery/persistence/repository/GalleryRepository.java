package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Gallery;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for the Gallery class/table
 *
 * @author TrusTio
 */
public interface GalleryRepository extends CrudRepository<Gallery, Integer> {
    Optional<Gallery> findById(Long id);

    Optional<Gallery> findByName(String name);

    Optional<Gallery> findByNameAndUserId(String name, Long userId);

    void delete(Gallery gallery);
}