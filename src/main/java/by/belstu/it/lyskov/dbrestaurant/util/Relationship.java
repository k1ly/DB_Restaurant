package by.belstu.it.lyskov.dbrestaurant.util;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Relationship {
    String refColumnName() default "";

    boolean isSortable() default true;
}
