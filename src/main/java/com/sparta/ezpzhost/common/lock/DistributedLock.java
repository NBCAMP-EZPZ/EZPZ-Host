package com.sparta.ezpzhost.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redisson Distributed Lock annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 락의 이름
     */
    String key();

    /**
     * 락의 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락 대기 시간
     * 락 획득을 위해 waitTime 만큼 대기합니다.
     */
    long waitTime() default 5L;

    /**
     * 락 임대 시간
     * 락을 획득한 이후 leaseTime 만큼 지나면 락을 해제합니다.
     */
    long leaseTime() default 3L;

}
