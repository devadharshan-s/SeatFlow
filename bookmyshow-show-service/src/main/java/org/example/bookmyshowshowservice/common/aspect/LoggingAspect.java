package org.example.bookmyshowshowservice.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging execution of service and controller Spring beans.
 * Logs method entry, arguments, return values, latency, and exceptions.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Pointcut definition
    }

    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        log.info("▶ Enter: {}.{}() with argument[s] = {}", className, methodName, arguments);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.info("◀ Exit: {}.{}() returning = {} (took {}ms)", className, methodName, result, elapsedTime);
            return result;
        } catch (Throwable t) {
            long elapsedTime = System.currentTimeMillis() - start;
            log.error("⚠ Exception: {}.{}() thrown = {} with message = {} (took {}ms)", 
                    className, methodName, t.getClass().getSimpleName(), t.getMessage(), elapsedTime);
            throw t;
        }
    }
}
