package com.mine.gallery.persistence.repository;

import com.mine.gallery.persistence.entity.Gallery;
import org.springframework.data.repository.CrudRepository;

public interface GalleryRepository extends CrudRepository<Gallery, Integer> {

}