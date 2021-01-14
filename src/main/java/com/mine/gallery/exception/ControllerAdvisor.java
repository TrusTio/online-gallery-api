package com.mine.gallery.exception;

import com.mine.gallery.exception.gallery.GalleryNameTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class extends the {@link ResponseEntityExceptionHandler ResponseEntityExceptionHandler}
 * and handles the responses for the various exceptions/errors of the api.
 *
 * @author TrusTio
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    /**
     * Handles GalleryNameTakenException
     *
     * @param e
     * @param request
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(GalleryNameTakenException.class)
    public ResponseEntity<Object> handleGalleryNameTaken(
            GalleryNameTakenException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now())
                .setMessage(e.getMessage())
                .setDetail("No duplicate gallery names allowed.");

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
