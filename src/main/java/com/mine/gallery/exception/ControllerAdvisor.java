package com.mine.gallery.exception;

import com.mine.gallery.exception.gallery.GalleryNotFoundException;
import com.mine.gallery.exception.gallery.GalleryValidationException;
import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.exception.image.ImageNotFoundException;
import com.mine.gallery.exception.image.ImageValidationException;
import com.mine.gallery.exception.user.LoginException;
import com.mine.gallery.exception.user.SignUpValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
    @ExceptionHandler(GalleryValidationException.class)
    public ResponseEntity<Object> handleGalleryNameTaken(
            GalleryValidationException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles GalleryNotFoundException
     *
     * @param e       GalleryNotFoundException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(GalleryNotFoundException.class)
    public ResponseEntity<Object> handleGalleryNotFound(
            GalleryNotFoundException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage());

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

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles LoginException
     *
     * @param e       LoginException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> handleBadCredentials(
            LoginException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles ExpiredJwtException
     *
     * @param e       ExpiredJwtException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwt(
            ExpiredJwtException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles ImageNotFoundException
     *
     * @param e       ImageNotFoundException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<Object> handleImageNotFound(
            ImageNotFoundException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles UnauthorizedAccessException
     *
     * @param e       UnauthorizedAccessException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedAccess(
            UnauthorizedAccessException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles ImageValidationException
     *
     * @param e       ImageValidationException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(ImageValidationException.class)
    public ResponseEntity<Object> handleImageValidation(
            ImageValidationException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles AccessDeniedException thrown by @PreAuthorize method annotation
     *
     * @param e       AccessDeniedException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(
            AccessDeniedException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage());

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
