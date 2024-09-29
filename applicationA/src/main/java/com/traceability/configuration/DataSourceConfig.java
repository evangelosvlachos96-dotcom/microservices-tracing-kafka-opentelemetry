//package com.traceability.configuration;
//
//import io.opentelemetry.api.OpenTelemetry;
//import io.opentelemetry.instrumentation.jdbc.datasource.OpenTelemetryDataSource;
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Autowired
//    OpenTelemetry openTelemetry;
//
//    @Bean
//    public DataSource dataSource() {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
//        dataSource.setUsername("postgres");
//        dataSource.setPassword("postgres");
//        return new OpenTelemetryDataSource(dataSource);
//    }
//}
