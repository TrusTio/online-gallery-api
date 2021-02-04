package com.mine.gallery.validation;

import org.passay.IllegalCharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Gallery name Validator that implements {@link ConstraintValidator}.
 * Uses the passay library to validate the gallery name field.
 *
 * @author TrusTio
 */
public class GalleryNameValidator implements ConstraintValidator<ValidGalleryName, String> {

    @Override
    public boolean isValid(String galleryName, ConstraintValidatorContext context) {
        char[] illegalChars = ("~`!@#$%^&*()-+{}[]<>?/]\\").toCharArray();
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(1, 50),
                new IllegalCharacterRule(illegalChars)
        ));

        RuleResult result = validator.validate(new PasswordData(galleryName));

        return result.isValid();
    }
}
