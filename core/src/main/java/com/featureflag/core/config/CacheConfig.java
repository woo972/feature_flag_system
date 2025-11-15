package com.featureflag.core.config;

import com.featureflag.shared.config.JacksonConfig;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DirContextDnsResolver;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static io.lettuce.core.resource.Delay.fullJitter;

@Configuration
@EnableCaching
public class CacheConfig {

    public static class CacheConstant {
        public static final long DEFAULT_CACHE_TTL = 3600; // 1 hour in seconds
    }

    /**
     * Local cache configuration for development environment.
     * Uses in-memory ConcurrentMapCacheManager without Redis dependency.
     */
    @Slf4j
    @Configuration
    @Profile("local")
    public static class LocalCacheConfig {

        @Bean
        public CacheManager cacheManager() {
            log.info("Initializing ConcurrentMapCacheManager for local profile");
            return new ConcurrentMapCacheManager();
        }
    }

    /**
     * Redis cache configuration for production and other non-local environments.
     * Uses Redis with proper connection pooling and resilience settings.
     */
    @Slf4j
    @Configuration
    @Profile("!local")
    public static class RedisCacheConfig {

        @Value("${spring.data.redis.host:localhost}")
        private String host;

        @Value("${spring.data.redis.port:6379}")
        private int port;

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

            log.info("Initializing RedisCacheManager with host: {}", host);
            return RedisCacheManager
                    .builder(redisConnectionFactory)
                    .cacheDefaults(cacheConfiguration)
                    .build();
        }

        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

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

            log.info("LettuceConnectionFactory initialized with host: {}", host);
            return new LettuceConnectionFactory(config, clientConfig);
        }
    }
}



