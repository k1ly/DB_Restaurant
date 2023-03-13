package by.belstu.it.lyskov.dbrestaurant.util.page;

import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageImpl<T> implements Page<T> {
    private final List<T> content = new ArrayList<>();
    private final Pageable pageable;
    private final long total;

    public PageImpl(List<T> content, Pageable pageable, long total) {
        this.content.addAll(content);
        this.pageable = pageable;
        this.total = pageable.toOptional().filter(p -> !content.isEmpty() && p.getOffset() + p.getPageSize() > total)
                .map(p -> p.getOffset() + content.size()).orElse(total);
    }

    public boolean hasContent() {
        return !this.content.isEmpty();
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(this.content);
    }

    public Pageable getPageable() {
        return this.pageable;
    }

    public Sort getSort() {
        return this.pageable.getSort();
    }

    public int getNumber() {
        return this.pageable.getPageNumber();
    }

    public int getSize() {
        return this.pageable.isPaged() ? this.pageable.getPageSize() : this.content.size();
    }

    public int getNumberOfElements() {
        return this.content.size();
    }

    public long getTotalElements() {
        return this.total;
    }

    public int getTotalPages() {
        return this.getSize() == 0 ? 1 : (int) Math.ceil((double) this.total / (double) this.getSize());
    }

    public boolean hasNext() {
        return this.getNumber() + 1 < this.getTotalPages();
    }

    public boolean hasPrevious() {
        return this.getNumber() > 0;
    }

    public boolean isFirst() {
        return !this.hasPrevious();
    }

    public boolean isLast() {
        return !this.hasNext();
    }

    public Pageable nextPageable() {
        return this.hasNext() ? this.pageable.next() : null;
    }

    public Pageable previousPageable() {
        return this.hasPrevious() ? this.pageable.previous() : null;
    }

}
