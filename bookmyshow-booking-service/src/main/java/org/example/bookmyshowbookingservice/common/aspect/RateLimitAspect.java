package org.example.bookmyshowbookingservice.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.bookmyshowbookingservice.common.annotation.RateLimit;
import org.example.bookmyshowbookingservice.common.exception.RateLimitExceededException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * AOP aspect that enforces distributed rate limiting on methods annotated with {@link RateLimit}.
 *
 * <p>Uses Redisson's {@link RRateLimiter} backed by the shared Redis instance,
 * so all horizontally-scaled booking-service instances share the same rate counters.
 * The rate limiter key is scoped per-user (JWT email) or per-IP (for unauthenticated calls).</p>
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(rateLimit)")
    public Object enforce(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        String userKey = resolveUserKey();
        String rateLimiterKey = "ratelimit:" + methodName + ":" + userKey;

        RRateLimiter limiter = redissonClient.getRateLimiter(rateLimiterKey);

        // trySetRate(RateType, rate, Duration) is the non-deprecated API in Redisson 3.x
        // RateType.PER_CLIENT scopes the counter to this specific Redis key (our per-user key)
        Duration window = Duration.of(rateLimit.rateInterval(), rateLimit.rateIntervalUnit().toChronoUnit());
        limiter.trySetRate(RateType.PER_CLIENT, rateLimit.rate(), window);

        if (!limiter.tryAcquire()) {
            log.warn("Rate limit exceeded for user={} on method={} (limit={}/{}{})",
                    userKey, methodName, rateLimit.rate(), rateLimit.rateInterval(), rateLimit.rateIntervalUnit());
            throw new RateLimitExceededException(
                    "Rate limit exceeded: max " + rateLimit.rate() + " requests per "
                            + rateLimit.rateInterval() + " " + rateLimit.rateIntervalUnit().name().toLowerCase()
            );
        }

        return joinPoint.proceed();

    }

    /**
     * Resolves the current user's identity.
     * Authenticated users are identified by their JWT email claim (unique per account).
     * Unauthenticated requests fall back to the client IP address — each IP gets its own
     * separate rate limiter key, so two anonymous users never share a counter.
     */
    private String resolveUserKey() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
        }

        // Fall back to client IP so each unauthenticated caller gets an isolated limiter
        return resolveClientIp();

    }

    /**
     * Extracts the real client IP, respecting reverse proxies via X-Forwarded-For.
     */
    private String resolveClientIp() {

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "unknown";
        }

        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For can be a comma-separated list; the first is the original client
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();

    }
}
