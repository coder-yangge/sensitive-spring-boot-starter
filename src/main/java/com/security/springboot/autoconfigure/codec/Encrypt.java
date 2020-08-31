package com.security.springboot.autoconfigure.codec;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/27 13:52
 */
public interface Encrypt {

    /**
     * <p>加密处理</p>
     * @param data 原始数据
     * @return 加密信息
     */
    byte[] encrypt(byte[] data);

}
