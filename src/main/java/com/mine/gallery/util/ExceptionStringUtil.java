package com.mine.gallery.util;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;

public class ExceptionStringUtil {
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
