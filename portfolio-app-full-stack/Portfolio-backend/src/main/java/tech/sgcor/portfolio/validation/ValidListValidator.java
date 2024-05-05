package tech.sgcor.portfolio.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Objects;

public class ValidListValidator implements ConstraintValidator<IsValidList, List<String>> {

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return value.stream().allMatch(Objects::nonNull);
    }
}
