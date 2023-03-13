package by.belstu.it.lyskov.dbrestaurant.util.page;

import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageableDefault {

    int value() default 10;

    int size() default 10;

    int page() default 0;

    Sort.Direction direction() default Sort.Direction.ASC;

    String[] sort() default {};
}