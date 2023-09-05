package at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.UniqueEmail;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidatorImpl implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null) {
            return true;
        }

        try {
            userService.findApplicationUserByEmail(email);
            return false;
        } catch (NotFoundException e) {
            //email does not exist yet
        }
        return true;
    }
}
