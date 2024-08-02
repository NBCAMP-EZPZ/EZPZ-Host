package com.sparta.ezpzhost.common.config.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final static String REDIS_URL_PREFIX = "rediss://";

    private final RedisClusterProperties properties;

    @Value("${spring.data.redis.mode}")
    private String mode;

    @Bean
    public RedissonClient redissonClient() {
        switch (mode) {
            case "standalone" -> {
                return Redisson.create();
            }
            case "cluster" -> {
                Config config = new Config();
                List<String> nodeAddresses = properties.getNodes().stream()
                        .map(node -> REDIS_URL_PREFIX + node).toList();
                config.useClusterServers()
                        .setNodeAddresses(nodeAddresses);
                return Redisson.create(config);
            }
            default -> throw new IllegalArgumentException("Invalid redisson mode");
        }
    }

}