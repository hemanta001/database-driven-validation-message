package com.message.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class RedisConfig {
    private final RedisConfigProperties redisConfigProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisConfigProperties.getHost(), redisConfigProperties.getPort());
        config.setPassword(redisConfigProperties.getPassword());
        config.setDatabase(redisConfigProperties.getDatabase());
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisConfigProperties.getTimeout()))
                .build();
        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("inwardResponse", cacheConfiguration(true, true))
                .withCacheConfiguration("customEnum", cacheConfiguration(false, false))
                .build();
    }

    public RedisCacheConfiguration cacheConfiguration(boolean hasTtl, boolean disableNullValues) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        if (disableNullValues) {
            redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();
        }
        if (hasTtl) {
            redisCacheConfiguration.entryTtl(Duration.ofHours(redisConfigProperties.getInwardKeyTtl()));
        }
        return redisCacheConfiguration;
    }
}
