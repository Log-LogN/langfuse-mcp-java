package com.langfuse.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LangfuseMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(LangfuseMcpApplication.class, args);
    }
}
