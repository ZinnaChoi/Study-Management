package mogakco.StudyManagement.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletRequest;
import mogakco.StudyManagement.dto.MemberLoginRes;

import org.springframework.web.context.request.RequestContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final ThreadLocal<Long> dbStartTime = new ThreadLocal<>();

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @JsonFilter("ignoreFieldsFilter")
    class IgnoreFieldsFilterMixin {
    }

    @Value("${study.systemId}")
    protected String systemId;

    private String serializeObjectToJson(Object object) {
        try {
            if (object instanceof MemberLoginRes) {
                MemberLoginRes memberLoginRes = (MemberLoginRes) object;
                String token = memberLoginRes.getToken();
                if (token != null) {
                    memberLoginRes.setToken("**********");
                }
                String json = objectMapper.writeValueAsString(memberLoginRes);
                memberLoginRes.setToken(token);
                return json;
            } else {
                return objectMapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            logger.error("JSON serialization error", e);
            return "Error serializing object to JSON";
        }
    }

    @SuppressWarnings("null")
    @Around("execution(* mogakco.StudyManagement.controller..*(..))")
    public Object logControllerAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        startTime.set(start);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("Started API: {} {} in system {}",
                request.getMethod(),
                request.getRequestURL().toString(),
                systemId);

        Object result = joinPoint.proceed();
        String responseBodyJson = serializeObjectToJson(result);

        long executionTime = System.currentTimeMillis() - startTime.get();
        startTime.remove();

        logger.info("Completed API: {} with responseBody: {} in {} ms",
                request.getRequestURL().toString(),
                responseBodyJson,
                executionTime);

        return result;
    }

    @Before("execution(* mogakco.StudyManagement.repository..*(..))")
    public void logDbAccessStart(JoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        dbStartTime.set(start);
        logger.info("DB Access Start: {}", joinPoint.getSignature().getName());
    }

    @After("execution(* mogakco.StudyManagement.repository..*(..))")
    public void logDbAccessEnd(JoinPoint joinPoint) {
        long end = System.currentTimeMillis();
        long duration = end - dbStartTime.get();
        dbStartTime.remove();
        logger.info("DB Access End: {} took {} ms", joinPoint.getSignature().getName(), duration);
    }

}
