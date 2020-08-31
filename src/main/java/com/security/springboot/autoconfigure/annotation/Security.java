package com.security.springboot.autoconfigure.annotation;

//import com.security.springboot.autoconfigure.enums.SecurityEnum;

import java.lang.annotation.*;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 17:23
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Security {

    /**
     *  SecurityEnum type() default SecurityEnum.AES;
     */


    boolean required() default true;
}
