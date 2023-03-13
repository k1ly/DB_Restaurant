package by.belstu.it.lyskov.dbrestaurant.util;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String columnName() default "";

    boolean isNullable() default false;

    boolean isSortable() default true;
}
