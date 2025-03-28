package com.ll.playon.global.aspect;

import com.ll.playon.global.response.RsData;
import com.ll.playon.standard.util.LogUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseAspect {
    private final HttpServletResponse response;

    @Around("""
            (
                within
                (
                    @org.springframework.web.bind.annotation.RestController *
                )
                &&
                (
                    @annotation(org.springframework.web.bind.annotation.GetMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PostMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.PutMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.DeleteMapping)
                    ||
                    @annotation(org.springframework.web.bind.annotation.RequestMapping)
                )
            )
            ||
            @annotation(org.springframework.web.bind.annotation.ResponseBody)
            """)
    public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        LogUtil.logControllerRequest(log, className, methodName);

        Object proceed = joinPoint.proceed();

        if (proceed instanceof RsData<?> rsData) {
            LogUtil.logControllerResponse(log, className, methodName, rsData);

            response.setStatus(rsData.getResultCode().value());
        }

        return proceed;
    }
}