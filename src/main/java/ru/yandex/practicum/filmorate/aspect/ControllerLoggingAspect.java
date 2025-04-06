package ru.yandex.practicum.filmorate.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Aspect
public class ControllerLoggingAspect {

    public static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerMethods() {
    }

    @Around("restControllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentHttpRequest();
        String uri = (request != null) ? request.getRequestURI() : "Unidentified URI";
        String method = (request != null) ? request.getMethod() : "Unidentified method";
        String methodName = joinPoint.getSignature().toShortString();
        String args = formatMethodArguments((joinPoint));
        logger.info("Request {} received: {} to method {}; args {}", method, uri, methodName, args);

        try {
            Object result = joinPoint.proceed();
            logger.info("Request {} processed: {}", uri, result);
            return result;
        } catch (Throwable e) {
            logger.error("Exception in {}, error: {}", methodName, e.getMessage());
            throw e;
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    private String formatMethodArguments(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        return IntStream.range(0, args.length)
                .mapToObj(i -> paramNames[i] + "=" + args[i])
                .collect(Collectors.joining(", "));
    }

}
