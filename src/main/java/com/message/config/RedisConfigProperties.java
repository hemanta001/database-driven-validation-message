package com.message.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.data.redis")
@Configuration
@Data
public class RedisConfigProperties {
    private int database;
    private String host;
    private int port;
    private String username;
    private String password;
    private Integer inwardKeyTtl;
    private Integer timeout;
}
