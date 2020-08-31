package com.security.springboot.autoconfigure.codec;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.Data;

import java.security.KeyPair;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/27 12:29
 */
@Data
public class RSAProcessor implements SecurityProcessor {

    private String publicKey;

    private String privateKey;

    private RSA rsa;

    public RSAProcessor(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.rsa = new RSA(privateKey, publicKey);
    }

    @Override
    public byte[] encrypt(byte[] data) {
       return rsa.encrypt(data, KeyType.PublicKey);
    }

    @Override
    public byte[] decrypt(String text) {
        return rsa.decrypt(text, KeyType.PrivateKey);
    }

    public static void main(String[] args) {
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        byte[] encoded = pair.getPrivate().getEncoded();
        System.out.println(encoded);
        String encode = Base64Encoder.encode(encoded);
        System.out.println(encode);
        byte[] encoded1 = pair.getPublic().getEncoded();
        System.out.println(encoded1);
        String encode1 = Base64Encoder.encode(encoded1);
        System.out.println(encode1);
        String text = "测试加密数据";
        RSAProcessor converter = new RSAProcessor(encode1, encode);
        byte[] encrypt = converter.encrypt(StrUtil.bytes(text, CharsetUtil.CHARSET_UTF_8));
        String encodeHexStr = HexUtil.encodeHexStr(encrypt, false);
        System.out.println(encodeHexStr);
        byte[] decrypt = converter.decrypt(encodeHexStr);
        System.out.println(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
    }
}
