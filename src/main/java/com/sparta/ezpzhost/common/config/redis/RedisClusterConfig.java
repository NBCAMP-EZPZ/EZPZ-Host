package com.sparta.ezpzhost.common.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.internal.HostAndPort;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DnsResolvers;
import io.lettuce.core.resource.MappingSocketAddressResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
public class RedisClusterConfig {

    private final RedisClusterProperties properties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(properties.getNodes());
        clusterConfiguration.setMaxRedirects(properties.getMaxRedirects());

        // Topology 설정 - 변경 사항을 어떻게 감지하고 반응할지 설정
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers() // 노드들의 상태 변화가 발생하면 해당 내용을 반영
                .enablePeriodicRefresh(Duration.ofHours(1L)) // 주기적으로 노드들의 상태를 확인
                .build();

        ClientOptions clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .build();

        // redis host, port 설정
        MappingSocketAddressResolver resolver = MappingSocketAddressResolver.create(
                DnsResolvers.UNRESOLVED,
                hostAndPort -> HostAndPort.of(properties.getConnectIp(), hostAndPort.getPort())
        );

        ClientResources clientResources = ClientResources.builder()
                .socketAddressResolver(resolver)
                .build();

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .clientResources(clientResources)
                .commandTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
    }

}
