package com.mine.gallery.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author TrusTio
 */
@Documented
@Constraint(validatedBy = GalleryNameValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidGalleryName {
    String message() default "Invalid gallery name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
