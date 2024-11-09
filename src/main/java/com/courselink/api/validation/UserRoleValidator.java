package com.courselink.api.validation;

import com.courselink.api.entity.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class UserRoleValidator implements ConstraintValidator<AuthorityValidation, Role> {

    private Role[] roles;

    @Override
    public void initialize(AuthorityValidation constraintAnnotation) {
        this.roles = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
        if (role == null) {
            return true;
        }

        boolean valid = Arrays.asList(roles).contains(role);

        if (!valid) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("This role doesn't exist!")
                    .addConstraintViolation();
        }

        return valid;
    }
}