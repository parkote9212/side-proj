package com.pgc.sideproj.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AopLogger {

    //     Pointcut정의(적용 범위)
    @Pointcut("within(com.pgc.sideproj.controller..*)")
    public void controllerPackagePointcut() {

    }

    //    Pointcut이 적용되는 메소드의 실행 전후를 감싸서 로직을 실행
    @Around("controllerPackagePointcut()")
    public Object logApiProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();

        Object[] args = joinPoint.getArgs();

        log.info("요청: {} (인자: {})", methodName, args);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("{} 메서드에서 예외 발생: {}", methodName, t.getMessage());
            throw t;
        }

        log.info("응답: {} (결과: {})", methodName, result);

        return result;
    }

}
