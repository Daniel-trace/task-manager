package com.example.taskmanager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DueDateNotPastValidator implements ConstraintValidator<DueDateNotPast, LocalDate> {

    @Override
    public boolean isValid(LocalDate dueDate, ConstraintValidatorContext context) {
        // Null dates allowed (validation for required is separate)
        if (dueDate == null) {
            return true;
        }
        // Return true if dueDate is today or future
        return !dueDate.isBefore(LocalDate.now());
    }
}
