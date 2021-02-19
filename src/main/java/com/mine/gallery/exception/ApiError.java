package com.mine.gallery.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * This class is used to build a body for the response during exceptions.
 *
 * @author TrusTio
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ApiError {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private int error;
    private String message;
    private String detail;

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.error = status.value();
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.detail = "";
    }
}
