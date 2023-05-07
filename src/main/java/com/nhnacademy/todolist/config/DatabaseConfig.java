package com.nhnacademy.todolist.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {
    private final DatabaseProperties properties;

    @Bean
    public DataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(properties.getDriverClassName());
        basicDataSource.setUrl(properties.getUrl());
        basicDataSource.setUsername(properties.getUsername());
        basicDataSource.setPassword(properties.getPassword());
        basicDataSource.setInitialSize(properties.getInitialSize());
        basicDataSource.setMaxTotal(properties.getMaxTotal());
        basicDataSource.setMinIdle(properties.getMinIdle());
        basicDataSource.setMaxIdle(properties.getMaxIdle());
        basicDataSource.setTestOnBorrow(properties.isTestOnBorrow());
        if (properties.isTestOnBorrow()) {
            basicDataSource.setValidationQuery(basicDataSource.getValidationQuery());
        }
        return basicDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
