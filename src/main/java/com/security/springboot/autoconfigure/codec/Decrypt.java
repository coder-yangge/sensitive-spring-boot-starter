package com.security.springboot.autoconfigure.codec;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/27 13:54
 */
public interface Decrypt {

    /**
     * <p>解密处理</p>
     * @param text 加密数据
     * @return 原始信息
     */
    byte[] decrypt(String text);
}
