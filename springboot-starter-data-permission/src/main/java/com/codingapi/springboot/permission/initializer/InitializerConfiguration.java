package com.codingapi.springboot.permission.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author lorne
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ConditionalOnSingleCandidate(value = DataSource.class)
public class InitializerConfiguration {

    @Bean
    @Autowired(required = false)
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws SQLException {
        return new DataSourceInitializer(dataSource);
    }

}
