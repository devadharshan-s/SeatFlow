package org.example.bookmyshowshowservice.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CacheExceptionConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {

        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache,
                    Object key) {

                log.warn("Redis Cache GET failed for key: {} on cache: {}. Falling back to DB. Error: {}", key,
                        cache.getName(), exception.getMessage());

            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache,
                    Object key, Object value) {

                log.warn("Redis Cache PUT failed for key: {} on cache: {}. Error: {}", key, cache.getName(),
                        exception.getMessage());

            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache,
                    Object key) {

                log.warn("Redis Cache EVICT failed for key: {} on cache: {}. Error: {}", key, cache.getName(),
                        exception.getMessage());

            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {

                log.warn("Redis Cache CLEAR failed on cache: {}. Error: {}", cache.getName(), exception.getMessage());

            }

        };

    }

}