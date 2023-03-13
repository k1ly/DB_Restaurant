package by.belstu.it.lyskov.dbrestaurant.util.sort;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface SortDefault {
    String[] value() default {};

    String[] sort() default {};

    Sort.Direction direction() default Sort.Direction.ASC;

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface SortDefaults {
        SortDefault[] value();
    }
}