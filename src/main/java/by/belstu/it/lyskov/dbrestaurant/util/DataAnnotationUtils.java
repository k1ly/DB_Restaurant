package by.belstu.it.lyskov.dbrestaurant.util;

import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public abstract class DataAnnotationUtils {

    public static void assertPageableUniqueness(MethodParameter parameter) {
        Method method = parameter.getMethod();
        if (method == null) {
            throw new IllegalArgumentException(String.format("Method parameter %s is not backed by a method", parameter));
        } else {
            if (containsMoreThanOnePageableParameter(method)) {
                Annotation[][] annotations = method.getParameterAnnotations();
                assertQualifiersFor(method.getParameterTypes(), annotations);
            }

        }
    }

    private static boolean containsMoreThanOnePageableParameter(Method method) {
        boolean pageableFound = false;
        for (Class<?> type : method.getParameterTypes()) {
            if (type.equals(Pageable.class)) {
                if (pageableFound)
                    return true;
                pageableFound = true;
            }
        }
        return false;
    }

    public static Object getSpecificPropertyOrDefaultFromValue(Annotation annotation, String property) {
        Object propertyDefaultValue = AnnotationUtils.getDefaultValue(annotation, property);
        Object propertyValue = AnnotationUtils.getValue(annotation, property);
        Object result = ObjectUtils.nullSafeEquals(propertyDefaultValue, propertyValue) ? AnnotationUtils.getValue(annotation) : propertyValue;
        if (result == null)
            throw new IllegalStateException("Exepected to be able to look up an annotation property value but failed");
        else
            return result;
    }

    @Nullable
    public static String getQualifier(@Nullable MethodParameter parameter) {
        if (parameter == null)
            return null;
        else {
            MergedAnnotations annotations = MergedAnnotations.from(parameter.getParameter());
            MergedAnnotation<Qualifier> qualifier = annotations.get(Qualifier.class);
            return qualifier.isPresent() ? qualifier.getString("value") : null;
        }
    }

    public static void assertQualifiersFor(Class<?>[] parameterTypes, Annotation[][] annotations) {
        Set<String> values = new HashSet<>();
        for (int i = 0; i < annotations.length; ++i) {
            if (Pageable.class.equals(parameterTypes[i])) {
                Qualifier qualifier = findAnnotation(annotations[i]);
                if (qualifier == null)
                    throw new IllegalStateException("Ambiguous Pageable arguments in handler method; If you use multiple parameters of type Pageable you need to qualify them with @Qualifier");
                if (values.contains(qualifier.value()))
                    throw new IllegalStateException("Values of the user Qualifiers must be unique");
                values.add(qualifier.value());
            }
        }
    }

    @Nullable
    private static Qualifier findAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Qualifier)
                return (Qualifier) annotation;
        }
        return null;
    }
}