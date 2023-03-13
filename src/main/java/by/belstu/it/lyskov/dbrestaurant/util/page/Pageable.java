package by.belstu.it.lyskov.dbrestaurant.util.page;

import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.util.Optional;

public interface Pageable {

    static Pageable ofSize(int size) {
        return PageRequest.ofSize(size);
    }

    default boolean isPaged() {
        return true;
    }

    int getPageNumber();

    int getPageSize();

    long getOffset();

    Sort getSort();

    default Sort getSortOr(Sort sort) {
        return this.getSort().isSorted() ? this.getSort() : sort;
    }

    Pageable next();

    Pageable previous();

    Pageable first();

    boolean hasPrevious();

    Pageable withPage(int page);

    default Optional<Pageable> toOptional() {
        return Optional.of(this);
    }
}
