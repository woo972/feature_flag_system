package com.featureflag.core.config;

import com.featureflag.shared.config.*;
import io.lettuce.core.*;
import io.lettuce.core.resource.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.*;
import org.springframework.data.redis.serializer.*;
import java.time.*;
import java.util.concurrent.*;
import static io.lettuce.core.resource.Delay.fullJitter;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    public static class CacheConstant {
        public static final long DEFAULT_CACHE_TTL = 3600; // 1 hour in seconds
    }

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.profiles.active:local}")
    private String active;

    @Value("${spring.redis.redisson-prefix:redis://}")
    private String redissonPrefix;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(CacheConstant.DEFAULT_CACHE_TTL))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                ).serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(JacksonConfig.getObjectMapper())
                        )
                );

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

        if ("local".equals(active)) {
            return new LettuceConnectionFactory(config);
        }

        TimeoutOptions timeoutOptions = TimeoutOptions
                .builder()
                .fixedTimeout(Duration.ofSeconds(20))
                .timeoutCommands(true)
                .build();

        SocketOptions socketOptions = SocketOptions
                .builder()
                .keepAlive(true)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        ClientResources clientResources = DefaultClientResources
                .builder()
                .dnsResolver(new DirContextDnsResolver())
                .reconnectDelay(
                        fullJitter(
                                Duration.ofMillis(100),
                                Duration.ofSeconds(5),
                                100,
                                TimeUnit.MILLISECONDS
                        )
                ).build();

        ClientOptions clientOptions = ClientOptions
                .builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.ACCEPT_COMMANDS)
                .autoReconnect(true)
                .socketOptions(socketOptions)
                .timeoutOptions(timeoutOptions)
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration
                .builder()
                .clientResources(clientResources)
                .commandTimeout(Duration.ofSeconds(20))
                .clientOptions(clientOptions)
                .useSsl()
                .build();

        log.info("LettuceConnectionFactory is ready with host: {}", host);
        return new LettuceConnectionFactory(config, clientConfig);
    }
}



