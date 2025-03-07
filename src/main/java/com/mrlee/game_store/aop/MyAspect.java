package com.mrlee.game_store.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class MyAspect {

    @Around("execution(* com.mrlee.game_store.service..*.*(..))")
    public Object monitorExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Execution method: {}", joinPoint.getSignature().getName());

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            log.info("Parameter [{}]: {}", i + 1, args[i]);
        }

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        if (executionTime > 1000) {
            log.warn("[PERFORMANCE WARNING] Execution time = {}ms", executionTime);
        }

        return result;
    }
}
