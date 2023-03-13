package by.belstu.it.lyskov.dbrestaurant.util.page;

import java.lang.reflect.Method;
import java.util.Optional;

import by.belstu.it.lyskov.dbrestaurant.util.DataAnnotationUtils;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import by.belstu.it.lyskov.dbrestaurant.util.sort.SortArgumentResolver;
import by.belstu.it.lyskov.dbrestaurant.util.sort.SortHandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageableHandlerMethodArgumentResolver implements PageableArgumentResolver {
    private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s; Must not be less than one";
    private static final String DEFAULT_PAGE_PARAMETER = "page";
    private static final String DEFAULT_SIZE_PARAMETER = "size";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
    private static final SortArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
    private final SortArgumentResolver sortResolver;
    static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 20);
    private Pageable fallbackPageable;
    private String pageParameterName;
    private String sizeParameterName;
    private String prefix;
    private String qualifierDelimiter;
    private int maxPageSize;
    private boolean oneIndexedParameters;

    public PageableHandlerMethodArgumentResolver() {
        this(null);
    }

    public PageableHandlerMethodArgumentResolver(@Nullable SortArgumentResolver sortResolver) {
        this.fallbackPageable = DEFAULT_PAGE_REQUEST;
        this.pageParameterName = DEFAULT_PAGE_PARAMETER;
        this.sizeParameterName = DEFAULT_SIZE_PARAMETER;
        this.prefix = DEFAULT_PREFIX;
        this.qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;
        this.maxPageSize = DEFAULT_MAX_PAGE_SIZE;
        this.oneIndexedParameters = false;
        this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }

    public void setFallbackPageable(Pageable fallbackPageable) {
        this.fallbackPageable = fallbackPageable;
    }

    public boolean isFallbackPageable(Pageable pageable) {
        return this.fallbackPageable.equals(pageable);
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public int getMaxPageSize() {
        return this.maxPageSize;
    }

    public void setPageParameterName(String pageParameterName) {
        this.pageParameterName = pageParameterName;
    }

    protected String getPageParameterName() {
        return this.pageParameterName;
    }

    public void setSizeParameterName(String sizeParameterName) {
        this.sizeParameterName = sizeParameterName;
    }

    public String getSizeParameterName() {
        return this.sizeParameterName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? "_" : qualifierDelimiter;
    }

    public void setOneIndexedParameters(boolean oneIndexedParameters) {
        this.oneIndexedParameters = oneIndexedParameters;
    }

    public boolean isOneIndexedParameters() {
        return this.oneIndexedParameters;
    }

    private Pageable getPageable(MethodParameter methodParameter, @Nullable String pageString, @Nullable String pageSizeString) {
        DataAnnotationUtils.assertPageableUniqueness(methodParameter);
        Optional<Pageable> defaultOrFallback = this.getDefaultFromAnnotationOrFallback(methodParameter).toOptional();
        Optional<Integer> page = this.parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true);
        Optional<Integer> pageSize = this.parseAndApplyBoundaries(pageSizeString, this.maxPageSize, false);
        if ((page.isEmpty() || pageSize.isEmpty()) && defaultOrFallback.isEmpty())
            return Pageable.ofSize(0);
        else {
            int p = page.orElseGet(() -> defaultOrFallback.map(Pageable::getPageNumber).orElseThrow(IllegalStateException::new));
            int ps = pageSize.orElseGet(() -> defaultOrFallback.map(Pageable::getPageSize).orElseThrow(IllegalStateException::new));
            ps = ps < 1 ? defaultOrFallback.map(Pageable::getPageSize).orElseThrow(IllegalStateException::new) : ps;
            ps = Math.min(ps, this.maxPageSize);
            return PageRequest.of(p, ps, defaultOrFallback.map(Pageable::getSort).orElseGet(Sort::unsorted));
        }
    }

    protected String getParameterNameToUse(String source, @Nullable MethodParameter parameter) {
        StringBuilder builder = new StringBuilder(this.prefix);
        String value = DataAnnotationUtils.getQualifier(parameter);
        if (StringUtils.hasLength(value))
            builder.append(value).append(this.qualifierDelimiter);
        return builder.append(source).toString();
    }

    private Pageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {
        PageableDefault defaults = methodParameter.getParameterAnnotation(PageableDefault.class);
        return defaults != null ? getDefaultPageRequestFrom(methodParameter, defaults) : this.fallbackPageable;
    }

    private static Pageable getDefaultPageRequestFrom(MethodParameter parameter, PageableDefault defaults) {
        int defaultPageSize = (Integer) DataAnnotationUtils.getSpecificPropertyOrDefaultFromValue(defaults, DEFAULT_SIZE_PARAMETER);
        if (defaultPageSize < 1) {
            Method annotatedMethod = parameter.getMethod();
            throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
        } else {
            return defaults.sort().length == 0 ? PageRequest.of(defaults.page(), defaultPageSize) : PageRequest.of(defaults.page(), defaultPageSize, defaults.direction(), defaults.sort());
        }
    }

    private Optional<Integer> parseAndApplyBoundaries(@Nullable String parameter, int upper, boolean shiftIndex) {
        if (!StringUtils.hasText(parameter))
            return Optional.empty();
        else {
            try {
                int parsed = Integer.parseInt(parameter) - (this.oneIndexedParameters && shiftIndex ? 1 : 0);
                return Optional.of(parsed < 0 ? 0 : Math.min(parsed, upper));
            } catch (NumberFormatException var5) {
                return Optional.of(0);
            }
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        String page = webRequest.getParameter(this.getParameterNameToUse(this.getPageParameterName(), methodParameter));
        String pageSize = webRequest.getParameter(this.getParameterNameToUse(this.getSizeParameterName(), methodParameter));
        Sort sort = this.sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        Pageable pageable = this.getPageable(methodParameter, page, pageSize);
        return sort.isSorted() ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort) : pageable;
    }
}
