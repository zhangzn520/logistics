package com.yyhn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ZzGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZzGatewayApplication.class, args);
    }

}
