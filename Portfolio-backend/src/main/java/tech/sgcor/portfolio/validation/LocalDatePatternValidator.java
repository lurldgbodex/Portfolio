package tech.sgcor.portfolio.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDatePatternValidator implements ConstraintValidator<ValidLocalDate, LocalDate> {
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    @Override
    public void initialize(ValidLocalDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
            String formattedDate = value.format(formatter);
            LocalDate parsedDate = LocalDate.parse(formattedDate, formatter);
            return value.isEqual(parsedDate);
        } catch (Exception e) {
            return false;
        }
    }
}
