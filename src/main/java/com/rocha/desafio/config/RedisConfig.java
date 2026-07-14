package com.rocha.desafio.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // Serializador oficial usando o Builder fluído do Spring Data 4.
        // O método .create() inicializa tudo sob o capô com os pacotes corretos (Jackson 3).
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.create(builder -> {
            builder.customize(mapperBuilder -> {
                // Registra o JavaTimeModule implicitamente para ler ZonedDateTime
                mapperBuilder.findAndAddModules();
            });
        });

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1)) // Define o tempo de vida do cache para 1 dia
                .disableCachingNullValues()   // Protege o Redis de armazenar buscas nulas
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
