package org.example.bookmyshowshowservice.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
@Slf4j
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.timeout:6000ms}")
    private String redisTimeout;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {

        Config config = new Config();
        String address = "redis://" + redisHost + ":" + redisPort;
        
        int timeoutMs = 6000;
        if (redisTimeout != null) {
            String clean = redisTimeout.replaceAll("[^0-9]", "");
            if (!clean.isEmpty()) {
                timeoutMs = Integer.parseInt(clean);
            }
        }

        config.useSingleServer()
                .setAddress(address)
                .setTimeout(timeoutMs)
                .setConnectTimeout(timeoutMs);

        if (redisPassword != null && !redisPassword.isBlank()) {
            config.useSingleServer().setPassword(redisPassword);
        }

        try {
            return Redisson.create(config);
        } catch (Exception ex) {
            log.warn("Could not connect to Redis at {} during startup: {}. Initializing fallback proxy for Circuit Breaker fallback.", address, ex.getMessage());
            return createFallbackProxy();
        }

    }

    private RedissonClient createFallbackProxy() {
        return (RedissonClient) Proxy.newProxyInstance(
                RedissonClient.class.getClassLoader(),
                new Class<?>[]{RedissonClient.class},
                (proxy, method, args) -> {
                    if ("shutdown".equals(method.getName())) {
                        return null;
                    }
                    if ("isShutdown".equals(method.getName()) || "isTerminated".equals(method.getName())) {
                        return true;
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("equals".equals(method.getName())) {
                        return args != null && args.length > 0 && args[0] == proxy;
                    }
                    if ("toString".equals(method.getName())) {
                        return "RedissonClientFallbackProxy[" + redisHost + ":" + redisPort + "]";
                    }
                    throw new RedisConnectionException(
                            "Redis server is unreachable at " + redisHost + ":" + redisPort + ". Circuit breaker fallback active."
                    );
                }
        );
    }
}
