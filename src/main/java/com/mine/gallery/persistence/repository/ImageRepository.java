package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Image;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the Image class/table
 * @author TrusTio
 */
public interface ImageRepository extends CrudRepository<Image, Integer> {

}