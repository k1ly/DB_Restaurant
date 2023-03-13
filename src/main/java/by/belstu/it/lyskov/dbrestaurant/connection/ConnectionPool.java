package by.belstu.it.lyskov.dbrestaurant.connection;

import by.belstu.it.lyskov.dbrestaurant.config.ConnectionConfig;
import by.belstu.it.lyskov.dbrestaurant.exception.ConnectionException;

import java.sql.*;

public interface ConnectionPool {

    ConnectionConfig getConnectionConfig();

    Connection getConnection();

    void releaseConnection(Connection connection);

    void destroy() throws ConnectionException;
}
