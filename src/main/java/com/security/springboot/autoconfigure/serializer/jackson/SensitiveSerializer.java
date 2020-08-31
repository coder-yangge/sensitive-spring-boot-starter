package com.security.springboot.autoconfigure.serializer.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.security.springboot.autoconfigure.enums.SensitiveTypeEnum;
import com.security.springboot.autoconfigure.annotation.Sensitive2;
import com.security.springboot.autoconfigure.format.CommonDesensitize;
import lombok.Data;

import java.io.IOException;
import java.util.Objects;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/26 15:37
 */
@Data
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveTypeEnum typeEnum;

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        switch (this.typeEnum) {
            case CHINESE_NAME:
                jsonGenerator.writeString(CommonDesensitize.chineseName(s));
                break;
            case ID_CARD:
                jsonGenerator.writeString(CommonDesensitize.idCardNum(s));
                break;
            case FIXED_PHONE:
                jsonGenerator.writeString(CommonDesensitize.fixedPhone(s));
                break;
            case MOBILE_PHONE:
                jsonGenerator.writeString(CommonDesensitize.mobilePhone(s));
                break;
            case ADDRESS:
                jsonGenerator.writeString(CommonDesensitize.address(s, 8));
                break;
            case EMAIL:
                jsonGenerator.writeString(CommonDesensitize.email(s));
                break;
            case BANK_CARD:
                jsonGenerator.writeString(CommonDesensitize.bankCard(s));
                break;
            case PASSWORD:
                jsonGenerator.writeString(CommonDesensitize.password(s));
                break;
            case CARNUMBER:
                jsonGenerator.writeString(CommonDesensitize.carNumber(s));
                break;
            default:
                jsonGenerator.writeString(s);
                break;
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 为空直接跳过
        if (beanProperty != null) {
            // 非 String 类直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Sensitive2 sensitive = beanProperty.getAnnotation(Sensitive2.class);
                if (sensitive == null) {
                    sensitive = beanProperty.getContextAnnotation(Sensitive2.class);
                }
                //
                if (sensitive != null) {
                    this.setTypeEnum(sensitive.type());
                    return this;
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        } else {
            return serializerProvider.findNullValueSerializer(null);
        }
    }
}
