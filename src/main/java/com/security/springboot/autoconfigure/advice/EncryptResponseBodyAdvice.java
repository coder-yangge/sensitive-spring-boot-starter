package com.security.springboot.autoconfigure.advice;

import com.security.springboot.autoconfigure.SensitiveConfigProperties;
import com.security.springboot.autoconfigure.annotation.Security;
import com.security.springboot.autoconfigure.handler.SecurityHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 17:39
 */
@Slf4j
@ControllerAdvice
public class EncryptResponseBodyAdvice extends AbstractRequestResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    public EncryptResponseBodyAdvice(SensitiveConfigProperties properties) {
        STANDARD_CLASS.addAll(properties.getClassPackage());
        DEFAULT_CLEAN_DEPTH = properties.getMaxDeep();
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Security.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            return encrypt(body);
        } catch (Exception e) {
            log.error("encrypt failed ", e);
        }
        return null;
    }

    private Object encrypt(Object result) throws Exception {
        return handleObject(0, DEFAULT_CLEAN_DEPTH, null, result);
    }

    @Override
    public String handleSecurity(Field field, String value) {
        // 处理器分优先级，优先级高的处理器优先处理，只要找到一个支持的处理器，后续的处理器不再处理
        for (SecurityHandler securityHandler : securityHandlers) {
            if (field != null && securityHandler.support(field)) {
                Annotation annotation = securityHandler.acquire(field);
                return securityHandler.handleEncrypt(value, annotation);
            }
        }
        return value;
    }

}
