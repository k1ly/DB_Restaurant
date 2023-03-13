package by.belstu.it.lyskov.dbrestaurant.util.page;

import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.util.Collections;
import java.util.List;

public interface Page<T> {

    static <T> Page<T> empty(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0L);
    }

    boolean hasContent();

    List<T> getContent();

    default Pageable getPageable() {
        return PageRequest.of(this.getNumber(), this.getSize(), this.getSort());
    }

    Sort getSort();

    int getNumber();

    int getSize();

    int getNumberOfElements();

    long getTotalElements();

    int getTotalPages();

    boolean hasNext();

    boolean hasPrevious();

    boolean isFirst();

    boolean isLast();

    Pageable nextPageable();

    Pageable previousPageable();

    default Pageable nextOrLastPageable() {
        return this.hasNext() ? this.nextPageable() : this.getPageable();
    }

    default Pageable previousOrFirstPageable() {
        return this.hasPrevious() ? this.previousPageable() : this.getPageable();
    }
}
