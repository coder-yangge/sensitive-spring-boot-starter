package com.security.springboot.autoconfigure.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.security.springboot.autoconfigure.enums.SensitiveTypeEnum;
import com.security.springboot.autoconfigure.format.CommonDesensitize;
import com.security.springboot.autoconfigure.serializer.jackson.SensitiveSerializer;

import java.lang.annotation.*;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 12:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
@Documented
public @interface Sensitive2 {

    /**
     * 脱敏类型
     */
    SensitiveTypeEnum type();

    /**
     * 自定义规则
     */
    Class<?> value() default CommonDesensitize.class;
}
