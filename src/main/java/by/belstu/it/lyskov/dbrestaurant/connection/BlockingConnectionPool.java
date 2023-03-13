package by.belstu.it.lyskov.dbrestaurant.connection;

import by.belstu.it.lyskov.dbrestaurant.config.ConnectionConfig;
import by.belstu.it.lyskov.dbrestaurant.exception.ConnectionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class BlockingConnectionPool implements ConnectionPool {

    private final ConnectionConfig connectionConfig;
    @Value("${db.timeout}")
    private int dbAwaitTimeout;
    private final Map<String, BlockingQueue<Connection>> freeConnections;
    private final Map<String, BlockingQueue<Connection>> usingConnections;

    private BlockingConnectionPool(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
        freeConnections = new LinkedHashMap<>(this.connectionConfig.getUserConnections().size());
        usingConnections = new LinkedHashMap<>(this.connectionConfig.getUserConnections().size());
    }

    @PostConstruct
    private void openConnections() throws ConnectionException {
        try {
            if (connectionConfig == null)
                throw new ConnectionException("Connection configuration not found");
            else {
                Class.forName(connectionConfig.getDriver()).getDeclaredConstructor().newInstance();
                connectionConfig.getUserConnections().forEach(connectionInfo -> {
                    freeConnections.put("ROLE_" + connectionInfo.getUsername(), new LinkedBlockingQueue<>());
                    usingConnections.put("ROLE_" + connectionInfo.getUsername(), new LinkedBlockingQueue<>());
                    for (int i = 0; i < connectionInfo.getPoolSize(); i++) {
                        try {
                            Connection connection = DriverManager.getConnection(connectionConfig.getUrl(),
                                    connectionInfo.getPassword(), connectionInfo.getPassword());
                            freeConnections.get("ROLE_" + connectionInfo.getUsername()).add(connection);
                        } catch (SQLException e) {
                            throw new RuntimeException("Invalid connection configuration", e);
                        }
                    }
                });
            }
        } catch (ClassNotFoundException | InvocationTargetException
                 | InstantiationException | IllegalAccessException
                 | NoSuchMethodException e) {
            throw new ConnectionException("Unable to initialize driver manager", e);
        }
    }

    @Override
    public ConnectionConfig getConnectionConfig() {
        return this.connectionConfig;
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authority = "ROLE_GUEST";
            if (auth != null) {
                GrantedAuthority grantedAuthority = auth.getAuthorities().stream().findFirst()
                        .orElseThrow(() -> new AuthenticationServiceException("Could not find user authority"));
                authority = grantedAuthority.getAuthority();
            }
            connection = this.freeConnections.get(authority).poll(this.dbAwaitTimeout, TimeUnit.SECONDS);
            if (connection != null)
                this.usingConnections.get(authority).add(connection);
        } catch (InterruptedException exception) {
            log.error(exception.getMessage());
            Thread.currentThread().interrupt();
        }
        return connection;
    }

    @Override
    public void releaseConnection(Connection connection) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authority = "ROLE_GUEST";
        if (auth != null) {
            GrantedAuthority grantedAuthority = auth.getAuthorities().stream().findFirst()
                    .orElseThrow(() -> new AuthenticationServiceException("Could not find user authority"));
            authority = grantedAuthority.getAuthority();
        }
        if (this.usingConnections.get(authority).remove(connection))
            this.freeConnections.get(authority).add(connection);
    }

    @Override
    @PreDestroy
    public void destroy() throws ConnectionException {
        for (var set : this.usingConnections.entrySet()) {
            if (!set.getValue().isEmpty()) {
                log.error("Connections were not released");
                throw new ConnectionException("Connections were not released");
            }
        }
        for (var set : this.freeConnections.entrySet()) {
            for (int i = 0; i < set.getValue().size(); i++) {
                try {
                    Connection connection = set.getValue().take();
                    connection.close();
                } catch (InterruptedException | SQLException exception) {
                    log.error("Database connections were not closed");
                    throw new ConnectionException("Database connections were not closed", exception);
                }
            }
        }
    }
}
