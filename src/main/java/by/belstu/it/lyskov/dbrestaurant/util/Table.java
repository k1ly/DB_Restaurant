package by.belstu.it.lyskov.dbrestaurant.util;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String tableName() default "";
}
