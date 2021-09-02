//package com.pharm.pharmfinder.config;
//
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Bean
//    public DataSource postgresDataSource() {
//        String databaseUrl = System.getenv("DATABASE_URL");
//
//
//        URI dbUri;
//        try {
//            dbUri = new URI(databaseUrl);
//        }
//        catch (URISyntaxException e) {
//            System.out.println("Setting up DataSource failed.");
//            return null;
//        }
//
//        String username = dbUri.getUserInfo().split(":")[0];
//        String password = dbUri.getUserInfo().split(":")[1];
//        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
//                + dbUri.getPort() + dbUri.getPath();
//
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("org.postgresql.Driver");
//        dataSourceBuilder.url(dbUrl);
//        dataSourceBuilder.username(username);
//        dataSourceBuilder.password(password);
//        return dataSourceBuilder.build();
//    }
//}