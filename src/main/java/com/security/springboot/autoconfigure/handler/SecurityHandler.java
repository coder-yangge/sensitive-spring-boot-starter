package com.security.springboot.autoconfigure.handler;



import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/27 10:52
 */
public interface SecurityHandler<A extends Annotation> {

    boolean support(Field field);

    A acquire(Field field);

    String handleEncrypt(String source, A annotation);

    String handleDecrypt(String source, A annotation);
}
