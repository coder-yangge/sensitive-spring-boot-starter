package com.security.springboot.autoconfigure.codec;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import lombok.Data;

/**
 * AES的默认模式是：AES/ECB/PKCS5Padding
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/27 14:13
 */
@Data
public class AESProcessor implements SecurityProcessor {

    private String secret;

    private AES aes;

    public AESProcessor(String secret) {
        this.secret = secret;
        this.aes = SecureUtil.aes(SecureUtil.decode(secret));
    }

    @Override
    public byte[] decrypt(String text) {
        return aes.decrypt(text);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return aes.encrypt(data);
    }

    public static void main(String[] args) {
        //随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String encode = Base64Encoder.encode(key);
        System.out.println(encode);
        AESProcessor processor = new AESProcessor(encode);
        byte[] encrypt = processor.encrypt("测试数据加密".getBytes());
        String encode1 = Base64Encoder.encode(encrypt);
        System.out.println(encode1);
        byte[] decrypt = processor.decrypt(encode1);
        System.out.println(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
    }
}
