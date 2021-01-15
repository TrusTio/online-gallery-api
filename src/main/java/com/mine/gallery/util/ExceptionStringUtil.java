package com.mine.gallery.util;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;

/**
 * A class with utility methods for exception message building
 *
 * @author TrusTio
 */
public class ExceptionStringUtil {

    private ExceptionStringUtil() {
    }

    /**
     * Creates an ArrayList of ConstraintViolations and fills it using the {@link Errors Errors} errors parameter.
     * Then builds a string from it's violation messages
     *
     * @param errors {@link Errors Errors} errors to be for building a message
     * @return String message
     */
    public static String exceptionMessageBuilder(Errors errors) {
        // Extract ConstraintViolation list from body errors
        List<ConstraintViolation<?>> violationsList = new ArrayList<>();
        for (ObjectError e : errors.getAllErrors()) {
            violationsList.add(e.unwrap(ConstraintViolation.class));
        }

        StringBuilder stringBuilder = new StringBuilder();

        // Construct a helpful message for each input violation
        for (ConstraintViolation<?> violation : violationsList) {
            stringBuilder
                    .append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
