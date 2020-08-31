package com.security.springboot.autoconfigure.annotation;


import com.security.springboot.autoconfigure.SensitiveConfigurationSelector;
import com.security.springboot.autoconfigure.SensitivePropertiesAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/28 15:32
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SensitivePropertiesAutoConfiguration.class, SensitiveConfigurationSelector.class})
public @interface EnableSensitive {

    boolean security() default true;

    boolean sensitive() default true;

    String[] packages() default {};
}
