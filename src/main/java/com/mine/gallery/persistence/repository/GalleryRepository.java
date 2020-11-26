package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Gallery;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the Gallery class/table
 *
 * @author TrusTio
 */
public interface GalleryRepository extends CrudRepository<Gallery, Integer> {

}