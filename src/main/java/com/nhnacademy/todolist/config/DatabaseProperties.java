package com.nhnacademy.todolist.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@PropertySource(value = "classpath:/db.properties")
public class DatabaseProperties {
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;
    @Value("${db.driverClassName}")
    private String driverClassName;
    @Value("${db.url}")
    private String url;
    @Value("${db.initialSize}")
    private Integer initialSize;
    @Value("${db.maxTotal}")
    private Integer maxTotal;
    @Value("${db.maxIdle}")
    private Integer maxIdle;
    @Value("${db.minIdle}")
    private Integer minIdle;
    @Value("${db.maxWaitMillis}")
    private Integer maxWaitMillis;
    @Value("${db.validationQuery}")
    private String validationQuery;
    @Value("${db.testOnBorrow}")
    private boolean testOnBorrow;
}
