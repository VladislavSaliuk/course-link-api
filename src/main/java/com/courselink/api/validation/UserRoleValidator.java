package com.courselink.api.validation;

import com.courselink.api.entity.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class UserRoleValidator implements ConstraintValidator<UserRoleSubsetValidation, Role> {

    private Role[] roles;

    @Override
    public void initialize(UserRoleSubsetValidation constraintAnnotation) {
        this.roles = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
        return role == null || Arrays.asList(roles).contains(role);
    }
}