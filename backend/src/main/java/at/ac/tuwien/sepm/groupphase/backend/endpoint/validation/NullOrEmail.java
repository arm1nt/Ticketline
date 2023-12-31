package at.ac.tuwien.sepm.groupphase.backend.endpoint.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.impl.EmailOrNullValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailOrNullValidatorImpl.class)
public @interface NullOrEmail {
    String message() default "{javax.validation.constraints.Pattern.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
