package tech.sgcor.portfolio.shared;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDatePatternValidator.class)
public @interface ValidLocalDate {
    String message() default "Invalid date format. Provide date in pattern 'yyyy-MM-dd'.";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
