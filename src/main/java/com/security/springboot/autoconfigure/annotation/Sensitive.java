package com.security.springboot.autoconfigure.annotation;

import com.security.springboot.autoconfigure.enums.SensitiveTypeEnum;
import java.lang.annotation.*;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 12:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {

    /**
     * 脱敏类型
     */
    SensitiveTypeEnum type();

    boolean required() default true;

}
