package at.ac.tuwien.sepm.groupphase.backend.endpoint.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.impl.UniqueEmailValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidatorImpl.class)
public @interface UniqueEmail {
    String message() default "{Email is not unique}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}


