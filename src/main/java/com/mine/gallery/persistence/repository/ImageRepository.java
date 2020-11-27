package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for the Image class/table
 *
 * @author TrusTio
 */
public interface ImageRepository extends CrudRepository<Image, Integer> {
    Optional<Image> findById(Long id);

    Optional<Image> findByName(String name);
}