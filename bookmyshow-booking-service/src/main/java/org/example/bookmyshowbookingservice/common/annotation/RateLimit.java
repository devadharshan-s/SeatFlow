package org.example.bookmyshowbookingservice.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Enables distributed rate limiting on the annotated method using Redisson's RRateLimiter.
 * The rate limiter key is scoped per-user (resolved from the JWT email claim in SecurityContext).
 *
 * <p>Example: {@code @RateLimit(rate = 3, rateInterval = 60, rateIntervalUnit = TimeUnit.SECONDS)}
 * limits each user to 3 invocations per 60-second window.</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** Maximum number of permits (requests) allowed within the interval. */
    int rate();

    /** Duration of the rate-limiting window. */
    long rateInterval();

    /** Time unit for the rate interval. Defaults to seconds. */
    TimeUnit rateIntervalUnit() default TimeUnit.SECONDS;
}
