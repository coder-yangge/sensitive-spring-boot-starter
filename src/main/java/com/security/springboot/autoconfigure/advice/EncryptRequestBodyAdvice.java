package com.security.springboot.autoconfigure.advice;

import com.security.springboot.autoconfigure.SensitiveConfigProperties;
import com.security.springboot.autoconfigure.annotation.Security;
import com.security.springboot.autoconfigure.handler.SecurityHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 17:37
 */
@Slf4j
@ControllerAdvice
public class EncryptRequestBodyAdvice extends AbstractRequestResponseBodyAdvice implements RequestBodyAdvice {

    public EncryptRequestBodyAdvice(SensitiveConfigProperties properties) {
        STANDARD_CLASS.addAll(properties.getClassPackage());
        DEFAULT_CLEAN_DEPTH = properties.getMaxDeep();
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasParameterAnnotation(Security.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.debug("request body {}", body);
        try {
            return decrypt(body);
        } catch (Exception e) {
            log.error("decrypt failed ", e);
        }
        return null;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public String handleSecurity(Field field, String value) {
        // 处理器分优先级，优先级高的处理器优先处理，只要找到一个支持的处理器，后续的处理器不再处理
        for (SecurityHandler securityHandler : securityHandlers) {
            if (field != null && securityHandler.support(field)) {
                Annotation annotation = securityHandler.acquire(field);
                return securityHandler.handleDecrypt(value, annotation);
            }
        }
        return value;
    }

    private Object decrypt(Object body) throws Exception {
        return handleObject(0, DEFAULT_CLEAN_DEPTH, null, body);
    }

}
