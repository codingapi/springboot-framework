package com.codingapi.springboot.framework.domain.field;

import com.codingapi.springboot.framework.event.EventPusher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体代理
 */
@Slf4j
public class FieldValueInterceptor implements MethodInterceptor {

    // 目标类
    private final Class<?> targetClass;
    // 目标类构造函数参数类型
    private final Class<?>[] parameterTypes;
    // 目标类构造函数参数
    private final Object[] args;

    // 目标类实例
    private final Object target;
    // 目标类属性描述
    private final PropertyDescriptor[] propertyDescriptors;
    // 目标类属性值
    private final Map<String, Object> fields;

    public FieldValueInterceptor(Class<?> targetClass, Object... args) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        this.targetClass = targetClass;
        this.args = args;
        this.parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        this.target = targetClass.getConstructor(parameterTypes).newInstance(args);
        this.propertyDescriptors = BeanUtils.getPropertyDescriptors(targetClass);
        this.fields = new HashMap<>();
    }


    /**
     * 创建代理
     * @return 代理对象
     */
    public Object createProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(this);
        return enhancer.create(parameterTypes, args);
    }


    /**
     * 拦截方法
     * @param obj 代理对象
     * @param method 方法
     * @param args 参数
     * @param proxy 代理
     * @return 方法返回值
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 更新函数肯定有参数，如果没有参数，直接返回
        if (method.getParameterCount() <= 0) {
            return method.invoke(target, args);
        }

        if(fields.isEmpty()){
            this.readFields();
        }
        Object result = method.invoke(target, args);
        this.compareAndUpdateField();
        return result;
    }

    /**
     * 读取Entity字段
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException InvocationTargetException
     */
    private void readFields() throws InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            Object value = propertyDescriptor.getReadMethod().invoke(target);
            fields.put(name, value);
        }
    }

    /**
     * 对比字段
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException InvocationTargetException
     */
    private void compareAndUpdateField() throws InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            Object newValue = propertyDescriptor.getReadMethod().invoke(target);
            Object oldValue = fields.get(name);
            if (!newValue.equals(oldValue)) {
                pushEvent(name, oldValue, newValue);
            }
            fields.put(name, newValue);
        }
    }

    private void pushEvent(String field, Object oldValue, Object newValue) {
        FieldChangeEvent event = new FieldChangeEvent();
        event.setEntity(target);
        event.setSimpleName(targetClass.getSimpleName());
        event.setFieldName(field);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);
        event.setTimestamp(System.currentTimeMillis());
        EventPusher.push(event);
    }
}
