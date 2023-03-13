package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.connection.ConnectionPool;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.TransactionException;
import by.belstu.it.lyskov.dbrestaurant.util.Column;
import by.belstu.it.lyskov.dbrestaurant.util.Id;
import by.belstu.it.lyskov.dbrestaurant.util.Relationship;
import by.belstu.it.lyskov.dbrestaurant.util.Table;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

@Repository
public class ProcedureExecutor {

    private final ThreadLocal<Connection> transaction = new ThreadLocal<>();

    private final ConnectionPool connectionPool;

    public ProcedureExecutor(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void beginTransaction() throws TransactionException {
        if (transaction.get() == null) {
            Connection connection = connectionPool.getConnection();
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new TransactionException("Transaction begin error", e);
            }
            transaction.set(connection);
        }
    }

    public void commitTransaction() throws TransactionException {
        Connection connection = transaction.get();
        if (connection != null) {
            try {
                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new TransactionException("Transaction commit error", e);
            }
            connectionPool.releaseConnection(connection);
        }
    }

    public void rollbackTransaction() throws TransactionException {
        Connection connection = transaction.get();
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new TransactionException("Transaction rollback error", e);
            }
            connectionPool.releaseConnection(connection);
        }
    }

    public <T, ID> Optional<T> executeFindById(ID id, Class<T> resultType) throws SQLException, EntityMappingException, RepositoryException {
        Optional<T> optional = Optional.empty();
        if (resultType.isAnnotationPresent(Table.class)) {
            Table table = resultType.getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.FIND);
            List<Object> parameters = new ArrayList<>(List.of(id));
            optional = executeSingleResultQuery(procedure.getStatementCall(), resultType, parameters.toArray());
        }
        return optional;
    }

    public <T> List<T> executeSort(Sort sort, Class<T> resultType) throws SQLException, EntityMappingException, RepositoryException {
        List<T> list = null;
        if (resultType.isAnnotationPresent(Table.class)) {
            Table table = resultType.getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.SORT);
            list = executeSort(procedure.getStatementCall(), sort, resultType);
        }
        return list == null ? new ArrayList<>() : list;
    }

    public <T> List<T> executeSort(String statementCall, Sort sort, Class<T> resultType, Object... parameters) throws SQLException, EntityMappingException {
        List<Object> parameterList = new ArrayList<>(Arrays.stream(parameters).toList());
        parameterList.addAll(handleSort(sort, resultType.getDeclaredFields()));
        List<T> list = executeQuery(statementCall, resultType, parameterList.toArray());
        return list == null ? new ArrayList<>() : list;
    }

    public <T> List<T> executePage(Pageable pageable, Class<T> resultType) throws SQLException, EntityMappingException, RepositoryException {
        List<T> list = null;
        if (resultType.isAnnotationPresent(Table.class)) {
            Table table = resultType.getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.PAGE);
            list = executePage(procedure.getStatementCall(), pageable, resultType);
        }
        return list == null ? new ArrayList<>() : list;
    }

    public <T> List<T> executePage(String statementCall, Pageable pageable, Class<T> resultType, Object... parameters) throws SQLException, EntityMappingException {
        List<T> list = null;
        if (pageable.isPaged()) {
            List<Object> parameterList = new ArrayList<>(Arrays.stream(parameters).toList());
            parameterList.addAll(List.of(pageable.getOffset(), pageable.getPageSize()));
            parameterList.addAll(handleSort(pageable.getSortOr(Sort.unsorted()), resultType.getDeclaredFields()));
            list = executeQuery(statementCall, resultType, parameterList.toArray());
        }
        return list == null ? new ArrayList<>() : list;
    }

    public <T> Optional<T> executeSingleResultQuery(String statementCall, Class<T> resultType, Object... parameters) throws SQLException, EntityMappingException, RepositoryException {
        Optional<T> optional = Optional.empty();
        List<T> list = executeQuery(statementCall, resultType, parameters);
        if (list.size() > 0) {
            if (list.size() == 1)
                optional = Optional.of(list.get(0));
            else
                throw new RepositoryException("Inappropriate query result size");
        }
        return optional;
    }

    public <T> List<T> executeQuery(String statementCall, Class<T> resultType, Object... parameters) throws SQLException, EntityMappingException {
        List<T> list = new ArrayList<>();
        if (resultType.isAnnotationPresent(Table.class)) {
            Connection connection = connectionPool.getConnection();
            try (CallableStatement statement = connection.prepareCall(statementCall)) {
                ParameterMetaData metadata = statement.getParameterMetaData();
                for (int i = 0, j = 0; i < metadata.getParameterCount(); i++) {
                    statement.setObject(i + 1, j < parameters.length ? parameters[j++] : null);
                }
                list = retrieveResultList(statement.executeQuery(), resultType);
            } finally {
                connectionPool.releaseConnection(connection);
            }
        }
        return list;
    }

    public List<Object> handleSort(Sort sort, Field[] fields) {
        List<Object> parameters = new ArrayList<>();
        List<Sort.Order> orders = sort.getOrders();
        for (Sort.Order order : orders) {
            Arrays.stream(fields).filter(field -> {
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Relationship.class)) {
                    Column column = field.getDeclaredAnnotation(Column.class);
                    Relationship relationship = field.getDeclaredAnnotation(Relationship.class);
                    if (column != null && column.isSortable())
                        return column.columnName().equals(order.getAttribute());
                    if (relationship != null && relationship.isSortable())
                        return relationship.refColumnName().equals(order.getAttribute());
                }
                return false;
            }).findFirst().ifPresent(field -> {
                parameters.add(order.getAttribute());
                parameters.add(order.getDirection().toString());
            });
        }
        return parameters;
    }

    public <T> long executeCount(Class<T> resultType) throws SQLException, RepositoryException {
        if (resultType.isAnnotationPresent(Table.class)) {
            Table table = resultType.getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.COUNT);
            return (long) executeScalar(procedure.getStatementCall());
        }
        return -1;
    }

    public Object executeScalar(String statementCall, Object... parameters) throws SQLException {
        Connection connection = connectionPool.getConnection();
        try (CallableStatement statement = connection.prepareCall(statementCall)) {
            ParameterMetaData metadata = statement.getParameterMetaData();
            statement.registerOutParameter(1, metadata.getParameterType(1));
            for (int i = 0, j = 0; i < metadata.getParameterCount(); i++) {
                statement.setObject(i + 2, j < parameters.length ? parameters[j++] : null);
            }
            statement.execute();
            return statement.getObject(1);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    public <T> void executeInsert(T entity) throws SQLException, EntityMappingException, RepositoryException {
        if (entity.getClass().isAnnotationPresent(Table.class)) {
            Table table = entity.getClass().getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.INSERT);
            executeUpdate(procedure.getStatementCall(), takeFields(entity).values().toArray());
        }
    }

    public <T, ID> void executeUpdate(ID id, T entity) throws SQLException, EntityMappingException, RepositoryException {
        if (entity.getClass().isAnnotationPresent(Table.class)) {
            Table table = entity.getClass().getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.UPDATE);
            List<Object> parameters = new ArrayList<>(List.of(id));
            parameters.addAll(takeFields(entity).values());
            executeUpdate(procedure.getStatementCall(), parameters.toArray());
        }
    }

    public <T, ID> void executeDelete(ID id, Class<T> resultType) throws SQLException, EntityMappingException, RepositoryException {
        if (resultType.isAnnotationPresent(Table.class)) {
            Table table = resultType.getDeclaredAnnotation(Table.class);
            StoredStatement procedure = StoredStatement.on(table.tableName(), SqlOperation.DELETE);
            executeUpdate(procedure.getStatementCall(), id);
        }
    }

    public void executeUpdate(String statementCall, Object... parameters) throws SQLException {
        Connection connection = transaction.get() == null ? connectionPool.getConnection() : transaction.get();
        try (CallableStatement statement = connection.prepareCall(statementCall)) {
            for (int i = 0; i < statement.getParameterMetaData().getParameterCount(); i++)
                statement.setObject(i + 1, i < parameters.length ? parameters[i] : null);
            statement.executeUpdate();
        } finally {
            if (transaction.get() == null)
                connectionPool.releaseConnection(connection);
        }
    }

    public <T> Map<String, Object> takeFields(T entity) throws EntityMappingException {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (entity.getClass().isAnnotationPresent(Table.class)) {
            try {
                for (Field field : entity.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {
                        Column column = field.getDeclaredAnnotation(Column.class);
                        Object value = field.get(entity);
                        if (column.isNullable() || value != null)
                            fields.put(field.getDeclaredAnnotation(Column.class).columnName(), value);
                    }
                    if (field.isAnnotationPresent(Relationship.class)) {
                        Object value = field.get(entity);
                        if (value != null && field.getType().isAnnotationPresent(Table.class)) {
                            for (Field f : value.getClass().getDeclaredFields()) {
                                if (f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(Id.class)) {
                                    f.setAccessible(true);
                                    value = f.get(value);
                                    f.setAccessible(false);
                                    break;
                                }
                            }
                            fields.put(field.getDeclaredAnnotation(Relationship.class).refColumnName(), value);
                        }
                    }
                    field.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                throw new EntityMappingException("Error during reading entity fields", e);
            }
        }
        return fields;
    }

    private <T> List<T> retrieveResultList(ResultSet resultSet, Class<T> resultType) throws SQLException, EntityMappingException {
        List<T> list = new ArrayList<>();
        Map<String, Object> fields = new LinkedHashMap<>();
        var metadata = resultSet.getMetaData();
        while (resultSet.next()) {
            for (int i = 1; i <= metadata.getColumnCount(); i++)
                fields.put(metadata.getColumnName(i), resultSet.getObject(i));
            list.add(buildEntity(fields, resultType));
            fields.clear();
        }
        return list;
    }

    private <T> T buildEntity(Map<String, Object> fields, Class<T> resultType) throws EntityMappingException {
        T object = null;
        if (resultType.isAnnotationPresent(Table.class)) {
            try {
                object = resultType.getDeclaredConstructor().newInstance();
                for (Field field : resultType.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Column.class)) {
                        field.set(object, fields.get(field.getDeclaredAnnotation(Column.class).columnName()));
                    }
                    if (field.isAnnotationPresent(Relationship.class) && field.getType().isAnnotationPresent(Table.class)) {
                        Object entity = null;
                        Relationship relationship = field.getDeclaredAnnotation(Relationship.class);
                        if (fields.get(relationship.refColumnName()) != null) {
                            entity = field.getType().getDeclaredConstructor().newInstance();
                            for (Field f : field.getType().getDeclaredFields()) {
                                if (f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(Id.class)) {
                                    f.setAccessible(true);
                                    f.set(entity, fields.get(relationship.refColumnName()));
                                    f.setAccessible(false);
                                    break;
                                }
                            }
                        }
                        field.set(object, entity);
                    }
                    field.setAccessible(false);
                }
            } catch (InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                throw new EntityMappingException("Error during writing entity fields", e);
            }
        }
        return object;
    }

    public enum SqlOperation {
        FIND, SORT, PAGE, FILTER, INSERT, UPDATE, DELETE, COUNT
    }

    public enum StoredStatement {
        FIND_USER("users", SqlOperation.FIND, "{call find_user_by_id (?)}"),
        PAGE_USER("users", SqlOperation.PAGE, "{call get_users_paged (?,?,?,?)}"),
        COUNT_USER("users", SqlOperation.COUNT, "{? = call count_users ()}"),
        UPDATE_USER("users", SqlOperation.UPDATE, "{call update_user (?,?,?,?)}"),

        FIND_ROLE("roles", SqlOperation.FIND, "{call find_role_by_id (?)}"),
        SORT_ROLE("roles", SqlOperation.SORT, "{call get_roles_sorted (?,?)}"),

        FIND_DISH("dishes", SqlOperation.FIND, "{call find_dish_by_id (?)}"),
        PAGE_DISH("dishes", SqlOperation.PAGE, "{call get_dishes_paged (?,?,?,?)}"),
        COUNT_DISH("dishes", SqlOperation.COUNT, "{? = call count_dishes ()}"),
        INSERT_DISH("dishes", SqlOperation.INSERT, "{call add_dish (?,?,?,?,?,?,?)}"),
        UPDATE_DISH("dishes", SqlOperation.UPDATE, "{call update_dish (?,?,?,?,?,?,?,?)}"),
        DELETE_DISH("dishes", SqlOperation.DELETE, "{call delete_dish (?)}"),

        FIND_CATEGORY("categories", SqlOperation.FIND, "{call find_category_by_id (?)}"),
        SORT_CATEGORY("categories", SqlOperation.SORT, "{call get_categories_sorted (?,?)}"),
        PAGE_CATEGORY("categories", SqlOperation.PAGE, "{call get_categories_paged (?,?,?,?)}"),
        COUNT_CATEGORY("categories", SqlOperation.COUNT, "{? = call count_categories ()}"),
        INSERT_CATEGORY("categories", SqlOperation.INSERT, "{call add_category (?)}"),
        UPDATE_CATEGORY("categories", SqlOperation.UPDATE, "{call update_category (?,?)}"),
        DELETE_CATEGORY("categories", SqlOperation.DELETE, "{call delete_category (?)}"),

        FIND_ORDER("orders", SqlOperation.FIND, "{call find_order_by_id (?)}"),
        UPDATE_ORDER("orders", SqlOperation.UPDATE, "{call update_order (?,?,?)}"),

        FIND_STATUS("statuses", SqlOperation.FIND, "{call find_status_by_id (?)}"),

        FIND_ORDER_ITEM("order_items", SqlOperation.FIND, "{call find_order_item_by_id (?)}"),
        INSERT_ORDER_ITEM("order_items", SqlOperation.INSERT, "{call add_order_item (?,?,?)}"),
        UPDATE_ORDER_ITEM("order_items", SqlOperation.UPDATE, "{call update_order_item (?,?)}"),
        DELETE_ORDER_ITEM("order_items", SqlOperation.DELETE, "{call delete_order_item (?)}"),

        FIND_ADDRESS("addresses", SqlOperation.FIND, "{call find_address_by_id (?)}"),
        INSERT_ADDRESS("addresses", SqlOperation.INSERT, "{call add_address (?,?,?,?,?,?)}"),

        PAGE_REVIEW("reviews", SqlOperation.PAGE, "{call get_reviews_paged (?,?,?,?)}"),
        COUNT_REVIEW("reviews", SqlOperation.COUNT, "{? = call count_reviews ()}"),
        INSERT_REVIEW("reviews", SqlOperation.INSERT, "{call add_review (?,?,?)}");

        private final String tableName;
        private final SqlOperation sqlOperation;
        private final String statementCall;

        StoredStatement(String tableName, SqlOperation sqlOperation, String statementCall) {
            this.tableName = tableName;
            this.sqlOperation = sqlOperation;
            this.statementCall = statementCall;
        }

        public String getTableName() {
            return tableName;
        }

        public SqlOperation getSqlOperation() {
            return sqlOperation;
        }

        public String getStatementCall() {
            return statementCall;
        }

        public static StoredStatement on(String tableName, SqlOperation sqlOperation) throws RepositoryException {
            return Arrays.stream(StoredStatement.values())
                    .filter(p -> p.getTableName().equals(tableName) && p.getSqlOperation().equals(sqlOperation))
                    .findAny().orElseThrow(() -> new RepositoryException("Stored procedure or function doesn't exist"));
        }
    }
}
