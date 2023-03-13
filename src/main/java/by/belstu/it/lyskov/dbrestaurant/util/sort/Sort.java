package by.belstu.it.lyskov.dbrestaurant.util.sort;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.*;

@Data
public class Sort {
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;
    private static final Sort UNSORTED = new Sort(DEFAULT_DIRECTION, List.of());
    private final List<Order> orders;

    public Sort(List<Order> orders) {
        this.orders = orders;
    }

    private Sort(Direction direction, List<String> attributes) {
        this.orders = attributes.stream().map(a -> new Order(direction, a)).toList();
    }

    public static Sort by(String... attributes) {
        return attributes.length == 0 ? UNSORTED : new Sort(DEFAULT_DIRECTION, Arrays.asList(attributes));
    }

    public static Sort by(List<Order> orders) {
        return orders.isEmpty() ? unsorted() : new Sort(orders);
    }

    public static Sort by(Order[] orders) {
        return new Sort(Arrays.asList(orders));
    }

    public static Sort by(Direction direction, String... attributes) {
        return attributes.length == 0 ? UNSORTED : new Sort(direction, Arrays.asList(attributes));
    }

    public static Sort unsorted() {
        return UNSORTED;
    }

    private Sort withDirection(Direction direction) {
        return by(orders.stream().map(o -> o.with(direction)).toList());
    }

    public Sort ascending() {
        return withDirection(Direction.ASC);
    }

    public Sort descending() {
        return withDirection(Direction.DESC);
    }

    public boolean isSorted() {
        return !this.orders.isEmpty();
    }

    public boolean isEmpty() {
        return this.orders.isEmpty();
    }

    public boolean isUnsorted() {
        return !this.isSorted();
    }

    public Sort and(Sort sort) {
        List<Order> orders = new ArrayList<>(this.orders);
        orders.addAll(sort.orders);
        return by(orders);
    }

    public Order getOrderFor(String attribute) {
        return this.orders.stream().filter(o -> o.attribute.equals(attribute)).findAny().orElse(null);
    }

    @Data
    public static class Order {
        private final String attribute;
        private final Direction direction;

        public Order(@Nullable Direction direction, String attribute) {
            this.direction = direction;
            this.attribute = attribute;
        }

        public static Order by(String attribute) {
            return new Order(Sort.DEFAULT_DIRECTION, attribute);
        }

        public static Order asc(String attribute) {
            return new Order(Direction.ASC, attribute);
        }

        public static Order desc(String attribute) {
            return new Order(Direction.DESC, attribute);
        }

        public Direction getDirection() {
            return this.direction;
        }

        public String getAttribute() {
            return this.attribute;
        }

        public boolean isAscending() {
            return this.direction.isAscending();
        }

        public boolean isDescending() {
            return this.direction.isDescending();
        }

        public Order with(Direction direction) {
            return new Order(direction, this.attribute);
        }

        public Order withAttribute(String attribute) {
            return new Order(this.direction, attribute);
        }

        public Sort withAttributes(String... attributes) {
            return Sort.by(this.direction, attributes);
        }
    }

    public enum Direction {
        ASC,
        DESC;

        private Direction() {
        }

        public boolean isAscending() {
            return this.equals(ASC);
        }

        public boolean isDescending() {
            return this.equals(DESC);
        }

        public static Direction fromString(String value) {
            try {
                return valueOf(value.toUpperCase(Locale.US));
            } catch (Exception var2) {
                throw new IllegalArgumentException(String.format("Invalid value '%s' for orders given; Has to be either 'desc' or 'asc' (case insensitive)", value), var2);
            }
        }

        public static Optional<Direction> fromOptionalString(String value) {
            try {
                return Optional.of(fromString(value));
            } catch (IllegalArgumentException var2) {
                return Optional.empty();
            }
        }
    }
}
