package by.belstu.it.lyskov.dbrestaurant.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatcherValidator.class)
@Documented
public @interface PasswordMatcher {

    String message() default "{Password.mismatch}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
