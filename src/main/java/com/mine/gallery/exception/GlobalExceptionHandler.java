package com.mine.gallery.exception;

import com.mine.gallery.exception.gallery.GalleryNotFoundException;
import com.mine.gallery.exception.gallery.GalleryValidationException;
import com.mine.gallery.exception.generic.UnauthorizedAccessException;
import com.mine.gallery.exception.image.ImageNotFoundException;
import com.mine.gallery.exception.image.ImageValidationException;
import com.mine.gallery.exception.role.RoleNotFoundException;
import com.mine.gallery.exception.user.LoginException;
import com.mine.gallery.exception.user.SignUpValidationException;
import com.mine.gallery.exception.user.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.data.mapping.PropertyReferenceException;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@link GalleryValidationException}
     *
     * @param e       GalleryValidationException
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
     * Handles {@link GalleryNotFoundException}
     *
     * @param e       {@link GalleryNotFoundException}
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
     * Handles {@link SignUpValidationException}
     *
     * @param e       {@link SignUpValidationException}
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
     * Handles {@link LoginException}
     *
     * @param e       {@link LoginException}
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> handleBadCredentials(
            LoginException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link ImageNotFoundException}
     *
     * @param e       {@link ImageNotFoundException}
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
     * Handles {@link UserNotFoundException}
     *
     * @param e       {@link UserNotFoundException}
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(
            UserNotFoundException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link RoleNotFoundException}
     *
     * @param e       {@link RoleNotFoundException}
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Object> handleRoleNotFound(
            RoleNotFoundException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles {@link UnauthorizedAccessException}
     *
     * @param e       {@link UnauthorizedAccessException}
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
     * Handles {@link ImageValidationException}
     *
     * @param e       {@link ImageValidationException}
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
     * Handles MalformedJwtException and SignatureException
     *
     * @param e       MalformedJwtException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler({MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<Object> handleMalformedJwt(
            Exception e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage());

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

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage())
                .setDetail("Login again to get a valid token.");

        return buildResponseEntity(apiError);
    }

    /**
     * Handles IllegalArgumentException
     *
     * @param e       IllegalArgumentException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(
            IllegalArgumentException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

        return buildResponseEntity(apiError);
    }

    /**
     * Handles PropertyReferenceException
     *
     * @param e       PropertyReferenceException
     * @param request WebRequest
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> handlePropertyReference(
            PropertyReferenceException e, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());

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
