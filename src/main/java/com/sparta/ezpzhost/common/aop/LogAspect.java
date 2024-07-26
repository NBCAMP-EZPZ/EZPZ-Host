package com.sparta.ezpzhost.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 응답 시간 로깅 AOP
 */
@Aspect
@Component
@Slf4j(topic = "LogAspect")
public class LogAspect {

    @Pointcut("within(com.sparta.ezpzhost.domain.*.controller.*)")
    private void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("[{}] responseTime : {}ms",
                joinPoint.getSignature().toShortString(), endTime - startTime);
        return proceed;
    }
}
