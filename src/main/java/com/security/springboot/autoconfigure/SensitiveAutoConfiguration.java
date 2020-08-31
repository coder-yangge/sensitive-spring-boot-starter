package com.security.springboot.autoconfigure;

import com.security.springboot.autoconfigure.format.CommonDesensitize;
import com.security.springboot.autoconfigure.handler.SecurityHandler;
import com.security.springboot.autoconfigure.handler.SensitiveHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 10:44
 */
@Slf4j
@Configuration
public class SensitiveAutoConfiguration extends AbstractConfiguration{

    public SensitiveAutoConfiguration(SensitiveConfigProperties configProperties) {
       super(configProperties);
    }

    @Bean
    public SecurityHandler sensitiveHandler() {
        SensitiveHandler sensitiveHandler = new SensitiveHandler();
        sensitiveHandler.setDesensitize(new CommonDesensitize());
        return sensitiveHandler;
    }

}
