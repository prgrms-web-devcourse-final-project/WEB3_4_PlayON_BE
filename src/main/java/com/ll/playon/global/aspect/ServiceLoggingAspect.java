package com.ll.playon.global.aspect;

import com.ll.playon.standard.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceLoggingAspect {
    @Before("@within(org.springframework.stereotype.Service)")
    public Object logService(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        LogUtil.logServiceRequest(log, className, methodName);

        return joinPoint;
    }
}
