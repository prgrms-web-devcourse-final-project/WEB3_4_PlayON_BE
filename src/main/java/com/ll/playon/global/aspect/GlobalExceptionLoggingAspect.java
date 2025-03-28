package com.ll.playon.global.aspect;

import com.ll.playon.standard.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionLoggingAspect {
    @AfterThrowing(pointcut = "@within(org.springframework.stereotype.Service)", throwing = "ex")
    public void logGlobalException(JoinPoint joinPoint, Throwable ex) {
        LogUtil.logError(log, joinPoint, ex);
    }
}