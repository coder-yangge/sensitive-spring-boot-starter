package com.security.springboot.autoconfigure.advice;

import com.security.springboot.autoconfigure.handler.SecurityHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author yangge
 * @version 1.0.0
 * @date 2020/8/28 9:17
 */
@Slf4j
public abstract class AbstractRequestResponseBodyAdvice implements BeanFactoryAware, InitializingBean {

    public int DEFAULT_CLEAN_DEPTH;

    public List<String> STANDARD_CLASS = new ArrayList<>();

    protected ConfigurableListableBeanFactory beanFactory;

    protected List<SecurityHandler> securityHandlers = new ArrayList<>();

    public abstract String handleSecurity(Field field, String value);


    protected Object handleObject(int currentTime, int maxCleanDepth, Field parentField, Object result) throws Exception {
        if (result == null) {
            return null;
        }

        if (currentTime >= maxCleanDepth) {
            return result;
        }

        final int nextDepth = currentTime + 1;
        Class<?> resultClass = result.getClass();
        if (String.class.isAssignableFrom(resultClass)) {
            if (!securityHandlers.isEmpty()) {
                return handleSecurity(parentField, (String) result);
            }
            return result;
        } else if (List.class.isAssignableFrom(resultClass)) {
            wrapperNewObjList(parentField, (List<Object>) result, nextDepth);
            return result;

        } else if (Set.class.isAssignableFrom(resultClass)) {
            wrapperNewObjSet(parentField, (Set<Object>) result, nextDepth);
            return result;

        } else if (Map.class.isAssignableFrom(resultClass)) {
            wrapperNewObjMap(parentField, (Map<Object, Object>) result, nextDepth);
            return result;

        } else if (!isStandardClass(result)) {
            return result;
        }

        Field[] declaredFields = findAllDeclaredFields(resultClass);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object value = field.get(result);
            if (value == null) {
                continue;
            }

            Class<?> clazz = value.getClass();
            if (clazz.isAssignableFrom(Object.class)) {
                if (isStandardClass(value)) {
                    field.set(result, handleObject(nextDepth, maxCleanDepth, null, value));
                }
            } else if (String.class.isAssignableFrom(clazz)) {
                if (!securityHandlers.isEmpty()) {
                    field.set(result, handleSecurity(field, (String) value));
                }
            } else if (List.class.isAssignableFrom(clazz)) {
                wrapperNewObjList(field, (List<Object>) value, nextDepth);

            } else if (Set.class.isAssignableFrom(clazz)) {
                wrapperNewObjSet(field, (Set<Object>) value, nextDepth);

            } else if (Map.class.isAssignableFrom(clazz)) {
                wrapperNewObjMap(field, (Map<Object, Object>) value, nextDepth);

            } else if (isStandardClass(value)) {
                field.set(result, handleObject(nextDepth, maxCleanDepth, field, value));
            }
        }

        return result;
    }

    private Field[] findAllDeclaredFields(Class<?> resultClass) {
        Set<Field> fields = new HashSet<>();
        Class<?> currentClass = resultClass;
        do {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null && isStandardClass(currentClass));
        return fields.toArray(new Field[0]);
    }

    private void wrapperNewObjList(Field parentField, List<Object> valueList, int nextDepth) throws Exception {
        for (int i = 0; i < valueList.size(); i++) {
            try {
                valueList.set(i, handleObject(nextDepth, DEFAULT_CLEAN_DEPTH, parentField, valueList.get(i)));
            } catch (UnsupportedOperationException e) {
                log.error("value:{} class:{} is unModify!", valueList, valueList.getClass().getSimpleName());
                return;
            }
        }
    }

    private void wrapperNewObjMap(Field parentField, Map<Object, Object> objectMap, int nextDepth) throws Exception {
        for (Object key : objectMap.keySet()) {
            try {
                objectMap.put(key, handleObject(nextDepth, DEFAULT_CLEAN_DEPTH, parentField, objectMap.get(key)));
            } catch (UnsupportedOperationException e) {
                log.error("value:{} class:{} is unModify!", objectMap, objectMap.getClass().getSimpleName());
            }
        }
    }

    private void wrapperNewObjSet(Field parentField, Set<Object> objectSet, int nextDepth) throws Exception {
        List<Object> objectList = new LinkedList<>();
        for (Object obj : objectSet) {
            objectList.add(handleObject(nextDepth, DEFAULT_CLEAN_DEPTH, parentField, obj));
        }
        try {
            objectSet.clear();
        } catch (UnsupportedOperationException e) {
            log.error("value:{} class:{} is unModify!", objectSet, objectSet.getClass().getSimpleName());
        }
        objectSet.addAll(objectList);
    }

    boolean isStandardClass(Object result) {
        Class<?> clazz;
        if (result instanceof Class) {
            clazz = (Class<?>) result;
        } else {
            clazz = result.getClass();
        }
        for (String standardClass : STANDARD_CLASS) {
            if (clazz.getName().startsWith(standardClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AdvisorAutoProxyCreator requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    private static void sortProcessors(List<?> processors, ConfigurableListableBeanFactory beanFactory) {
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        processors.sort(comparatorToUse);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] processorNames = beanFactory.getBeanNamesForType(SecurityHandler.class, true, false);
        List<String> orderedHandlerNames = new ArrayList<>();
        List<String> nonOrderedHandlerNames = new ArrayList<>();
        for (String ppName : processorNames) {
            if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedHandlerNames.add(ppName);
            }
            else {
                nonOrderedHandlerNames.add(ppName);
            }
        }
        List<SecurityHandler> orderedHandlers = new ArrayList<>(orderedHandlerNames.size());
        for (String ppName : orderedHandlerNames) {
            SecurityHandler pp = beanFactory.getBean(ppName, SecurityHandler.class);
            orderedHandlers.add(pp);
        }
        sortProcessors(orderedHandlers, beanFactory);

        List<SecurityHandler> nonOrderedHandlers = new ArrayList<>(nonOrderedHandlerNames.size());
        for (String ppName : nonOrderedHandlerNames) {
            SecurityHandler pp = beanFactory.getBean(ppName, SecurityHandler.class);
            nonOrderedHandlers.add(pp);
        }
        this.securityHandlers.addAll(orderedHandlers);
        this.securityHandlers.addAll(nonOrderedHandlers);
    }
}
