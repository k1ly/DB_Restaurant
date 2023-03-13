package by.belstu.it.lyskov.dbrestaurant.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("db")
public class ConnectionConfig {

    private String driver;
    private String url;
    private final List<UserConnectionInfo> userConnections = new ArrayList<>();

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<UserConnectionInfo> getUserConnections() {
        return userConnections;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserConnectionInfo {
        private String username;
        private String password;
        private int poolSize;
    }
}
