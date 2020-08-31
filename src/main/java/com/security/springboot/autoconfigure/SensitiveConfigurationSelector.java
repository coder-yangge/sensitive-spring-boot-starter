package com.security.springboot.autoconfigure;

import com.security.springboot.autoconfigure.annotation.EnableSensitive;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/28 17:22
 */
public class SensitiveConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes enableSensitive = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableSensitive.class.getName(), false));
        if (enableSensitive == null) {
            throw new IllegalArgumentException(
                    "@EnableSensitive is not present on importing class " + importingClassMetadata.getClassName());
        }
        boolean security = enableSensitive.getBoolean("security");
        boolean sensitive = enableSensitive.getBoolean("sensitive");
        List<String> configuration = new ArrayList<>(2);
        if (security) {
            configuration.add(SecurityAutoConfiguration.class.getName());
        }
        if (sensitive) {
            configuration.add(SensitiveAutoConfiguration.class.getName());
        }
        if (configuration.isEmpty()) {
            return null;
        }

        return configuration.toArray(new String[configuration.size()]);
    }
}
