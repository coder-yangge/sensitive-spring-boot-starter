package com.security.springboot.autoconfigure;

import com.security.springboot.autoconfigure.advice.EncryptRequestBodyAdvice;
import com.security.springboot.autoconfigure.advice.EncryptResponseBodyAdvice;
import com.security.springboot.autoconfigure.codec.AESProcessor;
import com.security.springboot.autoconfigure.codec.SecurityProcessor;
import com.security.springboot.autoconfigure.enums.SecurityMode;
import com.security.springboot.autoconfigure.handler.CommonSecurityHandler;
import com.security.springboot.autoconfigure.handler.SecurityHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/28 16:27
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration extends AbstractConfiguration{

    public static final String BASE64 = "BASE64";

    public static final String HEX = "HEX";

    public SecurityAutoConfiguration(SensitiveConfigProperties configProperties) {
        super(configProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.sensitive.security", name = "type", havingValue = "AES", matchIfMissing = true)
    public SecurityProcessor aesProcessor(SensitiveConfigProperties configProperties) {
        return new AESProcessor(configProperties.getSecurity().getSecret());
    }

    @Bean
    public SecurityHandler commonSecurityHandler(SecurityProcessor securityProcessor, SensitiveConfigProperties configProperties) {
        String charsetName = configProperties.getSecurity().getCharset();
        String mode = configProperties.getSecurity().getMode();
        SecurityMode securityMode = null;
        try {
            Charset charset = Charset.forName(charsetName);
            if (BASE64.equalsIgnoreCase(mode)) {
                securityMode = SecurityMode.BASE64;
            } else if (HEX.equalsIgnoreCase(mode)) {
                securityMode = SecurityMode.HEX;
            } else {
                throw new RuntimeException("Unsupported mode " + mode);
            }
            CommonSecurityHandler securityHandler = new CommonSecurityHandler(securityProcessor, securityMode);
            securityHandler.setCharset(charset);
            return securityHandler;
        } catch (UnsupportedCharsetException e) {
            log.error("Unsupported charset name {}", charsetName);
            throw e;
        }
    }
}
