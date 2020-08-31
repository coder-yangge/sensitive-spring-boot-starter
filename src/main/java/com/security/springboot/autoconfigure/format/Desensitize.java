package com.security.springboot.autoconfigure.format;


import com.security.springboot.autoconfigure.annotation.Sensitive;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 12:42
 */
public interface Desensitize {

    /**
     * 脱敏处理
     * @param text 敏感信息
     * @return 脱敏后的信息
     */
    String format(String text, Sensitive sensitive);
}
