package by.belstu.it.lyskov.dbrestaurant.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface Password {

    String message() default "{password.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
