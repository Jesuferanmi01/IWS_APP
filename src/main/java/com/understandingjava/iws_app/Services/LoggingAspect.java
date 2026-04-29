package com.understandingjava.iws_app.Services;


import com.understandingjava.iws_app.Models.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final EventLogService eventLog;
    private final ObjectMapper mapper;

    @Around("@annotation(com.understandingjava.iws_app.Models.Logged)")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature  = (MethodSignature) pjp.getSignature();
        Method          method     = signature.getMethod();
        Logged          annotation = method.getAnnotation(Logged.class);

        String service    = annotation.service().isBlank()
                ? pjp.getTarget().getClass().getSimpleName()
                : annotation.service();
        String methodName = method.getName();


        eventLog.action(service, methodName + "() — called", serializeSafely(pjp.getArgs()));


        try {
            Object result = pjp.proceed();


            eventLog.event(service, methodName + "() — returned", serializeSafely(result));

            return result;

        } catch (Throwable ex) {
            eventLog.event(service, methodName + "() — threw",
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    private String serializeSafely(Object value) {
        if (value == null) return "null";
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            return value.toString();
        }
    }
}