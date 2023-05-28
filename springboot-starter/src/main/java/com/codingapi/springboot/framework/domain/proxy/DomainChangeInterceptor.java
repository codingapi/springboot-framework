package com.codingapi.springboot.framework.domain.proxy;

import com.codingapi.springboot.framework.domain.event.DomainChangeEvent;
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
public class DomainChangeInterceptor implements MethodInterceptor {

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

    public DomainChangeInterceptor(Class<?> targetClass, Object... args) throws NoSuchMethodException,
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
            this.readFields(fields,target,propertyDescriptors);
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
    private void readFields(Map<String,Object> fields, Object target,PropertyDescriptor[] propertyDescriptors) throws InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            Object value = propertyDescriptor.getReadMethod().invoke(target);
            if (isPrimitive(value)) {
                fields.put(name, value);
            }else {
                Map<String,Object> childFields = new HashMap<>();
                this.readFields(childFields,value,BeanUtils.getPropertyDescriptors(value.getClass()));
                fields.put(name, childFields);
            }
        }
    }


    private boolean isPrimitive(Object obj) {
        return obj instanceof String || obj instanceof Integer || obj instanceof Long
                || obj instanceof Double || obj instanceof Float || obj instanceof Boolean
                || obj instanceof Short || obj instanceof Byte || obj instanceof Character
                || obj instanceof Enum || obj instanceof Class;
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
            if(isPrimitive(newValue)) {
                if (!newValue.equals(oldValue)) {
                    pushEvent(name, oldValue, newValue);
                }
                fields.put(name, newValue);
            }else{
                Map<String,Object> newFields = new HashMap<>();
                this.readFields(newFields,newValue,BeanUtils.getPropertyDescriptors(newValue.getClass()));

                Map<String,Object> oldFields = (Map<String,Object>)oldValue;
                for (String key:oldFields.keySet()){
                    Object oldChildValue = oldFields.get(key);
                    Object newChildValue = newFields.get(key);
                    if(!oldChildValue.equals(newChildValue)){
                        String namePrefix = name + ".";
                        pushEvent(namePrefix+key, oldChildValue, newChildValue);
                    }
                }
                fields.put(name, newFields);
            }

        }
    }

    private void pushEvent(String fieldName, Object oldValue, Object newValue) {
        EventPusher.push(new DomainChangeEvent(target,fieldName,oldValue,newValue));
    }
}
