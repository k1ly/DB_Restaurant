package by.belstu.it.lyskov.dbrestaurant.util;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    Class<?> type() default void.class;
}
