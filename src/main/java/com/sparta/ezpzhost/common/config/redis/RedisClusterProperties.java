package com.sparta.ezpzhost.common.config.redis;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
public class RedisClusterProperties {

    private int maxRedirects;
    private String connectIp;
    private List<String> nodes;

}
