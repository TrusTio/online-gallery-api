package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the {@link Gallery} class/table
 *
 * @author TrusTio
 */
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {
    Optional<Gallery> findById(Long id);

    Optional<Gallery> findByIdAndUserId(Long id, Long userId);

    Optional<Gallery> findByName(String name);

    Optional<Gallery> findByNameAndUserId(String name, Long userId);

    Page<Gallery> findAllByUserId(Long userId, Pageable pageable);

    List<Gallery> findAllByUserId(Long userId);

    void delete(Gallery gallery);
}