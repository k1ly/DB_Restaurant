package by.belstu.it.lyskov.dbrestaurant.util.sort;

import by.belstu.it.lyskov.dbrestaurant.util.DataAnnotationUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SortHandlerMethodArgumentResolver implements SortArgumentResolver {
    private static final String DEFAULT_PARAMETER = "sort";
    private static final String DEFAULT_PROPERTY_DELIMITER = ",";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final Sort DEFAULT_SORT = Sort.unsorted();
    private static final String SORT_DEFAULTS_NAME = SortDefault.SortDefaults.class.getSimpleName();
    private static final String SORT_DEFAULT_NAME = SortDefault.class.getSimpleName();
    private Sort fallbackSort;
    private String sortParameter;
    private String propertyDelimiter;
    private String qualifierDelimiter;

    public SortHandlerMethodArgumentResolver() {
        this.fallbackSort = DEFAULT_SORT;
        this.sortParameter = DEFAULT_PARAMETER;
        this.propertyDelimiter = DEFAULT_PROPERTY_DELIMITER;
        this.qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;
    }

    public void setSortParameter(String sortParameter) {
        this.sortParameter = sortParameter;
    }

    public void setPropertyDelimiter(String propertyDelimiter) {
        this.propertyDelimiter = propertyDelimiter;
    }

    public String getPropertyDelimiter() {
        return this.propertyDelimiter;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? "_" : qualifierDelimiter;
    }

    public void setFallbackSort(Sort fallbackSort) {
        this.fallbackSort = fallbackSort;
    }

    protected Sort getDefaultFromAnnotationOrFallback(MethodParameter parameter) {
        SortDefault.SortDefaults annotatedDefaults = parameter.getParameterAnnotation(SortDefault.SortDefaults.class);
        SortDefault annotatedDefault = parameter.getParameterAnnotation(SortDefault.class);
        if (annotatedDefault != null && annotatedDefaults != null)
            throw new IllegalArgumentException(String.format("Cannot use both @%s and @%s on parameter %s; Move %s into %s to define sorting order", SORT_DEFAULTS_NAME, SORT_DEFAULT_NAME, parameter.toString(), SORT_DEFAULT_NAME, SORT_DEFAULTS_NAME));
        else if (annotatedDefault != null)
            return this.appendOrCreateSortTo(annotatedDefault, Sort.unsorted());
        else if (annotatedDefaults == null)
            return this.fallbackSort;
        else {
            Sort sort = Sort.unsorted();
            for (SortDefault currentAnnotatedDefault : annotatedDefaults.value())
                sort = this.appendOrCreateSortTo(currentAnnotatedDefault, sort);
            return sort;
        }
    }

    private Sort appendOrCreateSortTo(SortDefault sortDefault, Sort sortOrNull) {
        String[] fields = (String[]) DataAnnotationUtils.getSpecificPropertyOrDefaultFromValue(sortDefault, DEFAULT_PARAMETER);
        if (fields.length == 0)
            return Sort.unsorted();
        else {
            List<Sort.Order> orders = new ArrayList<>(fields.length);
            for (String field : fields)
                orders.add(new Sort.Order(sortDefault.direction(), field));
            return sortOrNull.and(Sort.by(orders));
        }
    }

    private String getSortParameter(@Nullable MethodParameter parameter) {
        StringBuilder builder = new StringBuilder();
        String value = DataAnnotationUtils.getQualifier(parameter);
        if (StringUtils.hasLength(value))
            builder.append(value).append(this.qualifierDelimiter);
        return builder.append(this.sortParameter).toString();
    }

    private Sort parseParameterIntoSort(List<String> source, String delimiter) {
        List<Sort.Order> allOrders = new ArrayList<>();
        source.forEach(part -> {
            if (part != null) {
                SortOrderParser.parse(part, delimiter)
                        .parseDirection().forEachOrder(allOrders::add);
            }
        });
        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.equals(parameter.getParameterType());
    }

    @Override
    public Sort resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        String[] directionParameter = webRequest.getParameterValues(this.getSortParameter(parameter));
        if (directionParameter == null)
            return this.getDefaultFromAnnotationOrFallback(parameter);
        else
            return directionParameter.length == 1 && !StringUtils.hasText(directionParameter[0]) ?
                    this.getDefaultFromAnnotationOrFallback(parameter) :
                    this.parseParameterIntoSort(Arrays.asList(directionParameter), this.getPropertyDelimiter());
    }

    static boolean notOnlyDots(String source) {
        return StringUtils.hasText(source.replace(".", ""));
    }

    static class SortOrderParser {
        private final String[] elements;
        private final int lastIndex;
        private final Optional<Sort.Direction> direction;

        private SortOrderParser(String[] elements) {
            this(elements, elements.length, Optional.empty());
        }

        private SortOrderParser(String[] elements, int lastIndex, Optional<Sort.Direction> direction) {
            this.elements = elements;
            this.lastIndex = Math.max(0, lastIndex);
            this.direction = direction;
        }

        public static SortOrderParser parse(String part, String delimiter) {
            return new SortOrderParser(Arrays.stream(part.split(delimiter))
                    .filter(SortHandlerMethodArgumentResolver::notOnlyDots).toArray(String[]::new));
        }

        public SortOrderParser parseDirection() {
            Optional<Sort.Direction> direction = this.lastIndex > 0 ? Sort.Direction.fromOptionalString(this.elements[this.lastIndex - 1]) : Optional.empty();
            return new SortOrderParser(this.elements, this.lastIndex - (direction.isPresent() ? 1 : 0), direction);
        }

        public void forEachOrder(Consumer<? super Sort.Order> callback) {
            for (int i = 0; i < this.lastIndex; ++i) {
                this.toOrder(this.elements[i]).ifPresent(callback);
            }
        }

        private Optional<Sort.Order> toOrder(String property) {
            return StringUtils.hasText(property) ?
                    Optional.of(this.direction.map(d -> new Sort.Order(d, property)).orElseGet(() ->
                            Sort.Order.by(property))) : Optional.empty();
        }
    }
}