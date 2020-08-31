package com.security.springboot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/27 16:53
 */
@Data
public class SecurityProperties {

    /**
     * @See SecurityMode
     */
    private String mode = "BASE64";

    private String type = "AES";

    private String charset = "UTF-8";

    private String secret = "+6cuvzvyrFZpRG9pf3r7eQ==";

    private String privateKey;

    private String publicKey;
}
