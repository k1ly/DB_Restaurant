package by.belstu.it.lyskov.dbrestaurant.util.page;

import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import lombok.Data;

@Data
public class PageRequest implements Pageable {
    private final int page;
    private final int size;
    private Sort sort;

    public PageRequest(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        } else if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        } else {
            this.page = page;
            this.size = size;
        }
    }

    private PageRequest(int page, int size, Sort sort) {
        this(page, size);
        this.sort = sort;
    }

    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    public static PageRequest of(int page, int size, Sort.Direction direction, String... attributes) {
        return of(page, size, Sort.by(direction, attributes));
    }

    public static PageRequest ofSize(int size) {
        return of(0, size);
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public int getPageSize() {
        return this.size;
    }

    @Override
    public long getOffset() {
        return (long) this.page * this.size;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public boolean hasPrevious() {
        return this.page > 0;
    }

    @Override
    public PageRequest next() {
        return new PageRequest(this.page + 1, this.size, this.sort);
    }

    @Override
    public PageRequest previous() {
        return new PageRequest(this.page > 0 ? this.page - 1 : 0, this.size, this.sort);
    }

    @Override
    public PageRequest first() {
        return new PageRequest(0, this.size, this.sort);
    }

    @Override
    public PageRequest withPage(int page) {
        return new PageRequest(page, this.size, this.sort);
    }

    public PageRequest withSort(Sort sort) {
        return new PageRequest(this.page, this.size, sort);
    }

    public PageRequest withSort(Sort.Direction direction, String... attributes) {
        return new PageRequest(this.page, this.size, Sort.by(direction, attributes));
    }

}
