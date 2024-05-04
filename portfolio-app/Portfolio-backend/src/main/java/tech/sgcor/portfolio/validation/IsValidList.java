package tech.sgcor.portfolio.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidListValidator.class)
public @interface IsValidList {

    String message() default "Field must be a non-null List";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
