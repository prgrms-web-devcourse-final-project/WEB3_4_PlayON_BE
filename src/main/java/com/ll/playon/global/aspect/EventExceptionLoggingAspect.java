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
public class EventExceptionLoggingAspect {
    @AfterThrowing(pointcut = "@annotation(org.springframework.context.event.EventListener)", throwing = "ex")
    public void logEventException(JoinPoint joinPoint, Throwable ex) {
        LogUtil.logError(log, joinPoint, ex);
    }
}
