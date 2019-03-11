package com.hcy308.oauth2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Simon
 */
@Configuration
public class DbConfig {

    @Value("${jdbc.driverClassName}")
    private String driverClassName = "";

    @Value("${jdbc.url}")
    private String url = "";

    @Value("${jdbc.user}")
    private String user = "";

    @Value("${jdbc.pass}")
    private String pass = "";


    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        return dataSource;
    }

}
