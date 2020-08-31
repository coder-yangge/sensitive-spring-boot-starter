package com.security.springboot.autoconfigure.handler;

import com.security.springboot.autoconfigure.annotation.Sensitive;
import com.security.springboot.autoconfigure.format.Desensitize;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/28 12:24
 */
@Data
public class SensitiveHandler implements SecurityHandler<Sensitive> {

    private Desensitize desensitize;

    @Override
    public boolean support(Field field) {
        Sensitive annotation = field.getAnnotation(Sensitive.class);
        return annotation != null && annotation.required();
    }

    @Override
    public Sensitive acquire(Field field) {
        Sensitive annotation = null;
        if (field != null) {
            annotation = field.getAnnotation(Sensitive.class);
        }
        return annotation;
    }

    @Override
    public String handleEncrypt(String source, Sensitive annotation) {
        return desensitize.format(source, annotation);
    }

    @Override
    public String handleDecrypt(String source, Sensitive annotation) {
        return source;
    }
}
