package com.mrlee.game_store.aop;

import com.mrlee.game_store.common.HttpRetry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class MyAspect {

    //@Around: AOP 실행 전후 모두 제어할 수 있다.
    //@After, @Before

    @Around("execution(* com.mrlee.game_store.service.GameService.*(..))")
    public Object monitorExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("Execution method: {}", joinPoint.getSignature().getName());

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            log.info("Execution Parameter [{}]: {}", i + 1, args[i]);
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

    @Around("@annotation(httpRetry)")
    public Object handleHttpRetry(ProceedingJoinPoint joinPoint, HttpRetry httpRetry) throws Throwable {

        int maxRetryCount = httpRetry.value();
        Exception exception = null;

        log.info("handleHttpRetry: {}", joinPoint.getSignature().getName());

        for (int i = 0; i <= maxRetryCount; i++) {
            try {
                log.info("handleHttpRetry Count={}/{}", i, maxRetryCount);
                return joinPoint.proceed();
            } catch (Exception e) {
                log.info("HTTP 통신 오류:: {}", e.getMessage());
                exception = e;
                if (i == (maxRetryCount - 1)) {
                    Thread.sleep(1000);
                }
            }
        }

        throw exception != null ? exception : new RuntimeException("http 재시도 호출 로직에서 오류 발생.");
    }
}
