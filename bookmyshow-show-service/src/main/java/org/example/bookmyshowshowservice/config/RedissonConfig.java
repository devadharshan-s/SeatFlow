package org.example.bookmyshowshowservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

        return Redisson.create(config);

    }
}
