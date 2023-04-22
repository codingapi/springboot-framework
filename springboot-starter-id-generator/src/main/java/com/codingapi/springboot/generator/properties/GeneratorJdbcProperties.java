package com.codingapi.springboot.generator.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Setter
@Getter
public class GeneratorJdbcProperties {

    private String jdbcUrl = "jdbc:sqlite:db.db";

    private String jdbcDriver = "org.sqlite.JDBC";

    private String jdbcUsername = "sa";

    private String jdbcPassword = "sa";

    public DataSource openDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        return dataSource;
    }
}
