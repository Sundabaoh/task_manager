package org.taskmanager.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.taskmanager.exeptions.InvalidArgumentException;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionPool {
    private static final HikariDataSource dataSource;
    private static final String url;
    private static final  String name;
    private static final  String password;

    static {
        HikariConfig config = new HikariConfig();

        url = ConfigLoader.getProperty("db.url");
        name = ConfigLoader.getProperty("db.username");
        password = ConfigLoader.getProperty("db.password");
        if (url == null || name == null || password == null || url.trim().isEmpty() || name.trim().isEmpty() || password.trim().isEmpty()) {
            throw new InvalidArgumentException("Неверно заполнены поля необходимые для установления соединения с бд");
        }

        config.setJdbcUrl(url);
        config.setUsername(name);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
