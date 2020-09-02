package com.security.springboot.autoconfigure.codec;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/9/2 14:41
 */
public class DESProcessor implements SecurityProcessor {

    private String secret;

    private DES des;

    public DESProcessor(String secret) {
        this.secret = secret;
        this.des = SecureUtil.des(SecureUtil.decode(secret));
    }

    @Override
    public byte[] decrypt(String text) {
        return des.decrypt(text);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return des.encrypt(data);
    }
}

