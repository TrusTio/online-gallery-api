package com.mine.gallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the application. The only functionality
 * it has currently is connecting to a MySQL and creating a database.
 * You can also check the swagger ui on http://localhost:8080/swagger-ui.html
 * It can be found on http://localhost:8080/
 *
 * @author TrusTio
 */
@SpringBootApplication
public class GalleryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GalleryApplication.class, args);
    }

}
