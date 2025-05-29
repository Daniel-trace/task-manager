package com.example.taskmanager.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DueDateNotPastValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DueDateNotPast {
    String message() default "Due date cannot be in the past";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
