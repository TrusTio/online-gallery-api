package com.mine.gallery.exception;

import com.mine.gallery.exception.gallery.CreateGalleryValidationException;
import com.mine.gallery.exception.user.SignUpValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class extends the {@link ResponseEntityExceptionHandler ResponseEntityExceptionHandler}
 * and handles the responses for the various exceptions/errors of the api.
 *
 * @author TrusTio
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    /**
     * Handles CreateGalleryValidationException
     *
     * @param e       GalleryNameTakenException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(CreateGalleryValidationException.class)
    public ResponseEntity<Object> handleGalleryNameTaken(
            CreateGalleryValidationException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now())
                .setMessage(e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles SignUpValidationException
     *
     * @param e       SignUpValidationException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(SignUpValidationException.class)
    public ResponseEntity<Object> handleSignUpValidation(
            SignUpValidationException e, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now())
                .setMessage(e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles MethodArgumentNotValidException
     *
     * @param ex      MethodArgumentNotValidException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    /**
     * Builds a new {@link ResponseEntity ResponseEntity}
     *
     * @param apiError ApiError
     * @return ResponseEntity<Object>
     */
    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
