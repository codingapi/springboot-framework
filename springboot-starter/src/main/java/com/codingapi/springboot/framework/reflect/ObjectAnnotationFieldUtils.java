package com.codingapi.springboot.framework.reflect;

import com.codingapi.springboot.framework.reflect.pojo.AnnotationTargetField;
import com.codingapi.springboot.framework.reflect.pojo.AnnotationTargetFieldResult;
import lombok.Getter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 对象注解字段反射工具类
 */
public class ObjectAnnotationFieldUtils {

    /**
     * 查找对象的注解的字段 (仅支持简单数据类型的扫描)
     *
     * @param target          目标对象
     * @param annotationClass 注解类型
     * @param fieldType       字段类型
     * @param <T>   字段类型
     * @return 注解目标字段对象集合
     */
    public static <T> AnnotationTargetFieldResult<T> findFieldAnnotationValue(Object target, Class<? extends Annotation> annotationClass, Class<T> fieldType) {
        AnnotationTargetFieldHolder<T> annotationTargetFieldHolder = new AnnotationTargetFieldHolder<>(target, annotationClass);
        annotationTargetFieldHolder.scanner();
        return new AnnotationTargetFieldResult<>(annotationTargetFieldHolder.getResult());
    }


    /**
     * 是否简单数据结构
     * @param fieldType 字段类型
     */
    public static boolean isSimpleType(Class<?> fieldType) {
        return fieldType.isPrimitive()
                || ClassUtils.isPrimitiveOrWrapper(fieldType)
                || fieldType == String.class
                || fieldType.isEnum()
                || Number.class.isAssignableFrom(fieldType)
                || CharSequence.class.isAssignableFrom(fieldType);
    }

    /**
     * 注解目标字段持有对象
     * @param <T> 字段类型
     */
    private static class AnnotationTargetFieldHolder<T> {
        /**
         * 扫描对象
         */
        private final Object source;

        /**
         * 注解类型
         */
        @Getter
        private final Class<? extends Annotation> annotationClass;

        /**
         * 已扫描的对象记录
         */
        private final List<Object> scannerHistoryList;

        /**
         * 扫描结果数据
         */
        @Getter
        private final List<AnnotationTargetField<T>> result;

        public AnnotationTargetFieldHolder(Object source, Class<? extends Annotation> annotationClass) {
            this.source = source;
            this.scannerHistoryList = new ArrayList<>();
            this.result = new ArrayList<>();
            this.annotationClass = annotationClass;
        }

        /**
         * 添加匹配字段
         * @param field 字段
         * @param target 字段绑定对象
         * @param value 字段值
         */
        public void addFiled(Field field, Object target, T value) {
            this.result.add(new AnnotationTargetField<>(field, target, value));
        }

        /**
         * 扫描对象
         */
        public void scanner() {
            ObjectAnnotationScanner<T> scanner = new ObjectAnnotationScanner<>(source, this);
            scanner.scanner();
        }

        /**
         * 是否已经扫描
         * @param target 对象
         */
        public boolean hasHistory(Object target) {
            return this.scannerHistoryList.contains(target);
        }

        /**
         * 添加到扫描记录
         * @param target 对象
         */
        public void addHistory(Object target) {
            this.scannerHistoryList.add(target);
        }
    }

    /**
     * 对象注解扫描器
     * @param <T> 字段类型
     */
    private static class ObjectAnnotationScanner<T> {
        /**
         * 扫描对象
         */
        private final Object target;

        /**
         * 扫描对象类型
         */
        private final Class<?> targetClass;

        /**
         * 注解目标字段持有对象
         */
        private final AnnotationTargetFieldHolder<T> valueHolder;


        public ObjectAnnotationScanner(Object target, AnnotationTargetFieldHolder<T> valueHolder) {
            this.target = target;
            this.valueHolder = valueHolder;
            this.targetClass = target.getClass();
        }

        /**
         * 扫描对象
         */
        public void scanner() {
            if(ObjectAnnotationFieldUtils.isSimpleType(this.targetClass)) {
                return;
            }
            if (this.target instanceof Collection<?>) {
                for (Object item : (Collection) this.target) {
                    ObjectAnnotationScanner<T> scanner = new ObjectAnnotationScanner<>(item, this.valueHolder);
                    scanner.scanner();
                }
            } else if (this.target instanceof Map<?, ?>) {
                for (Object item : ((Map) this.target).values()) {
                    ObjectAnnotationScanner<T> groovyScriptParser = new ObjectAnnotationScanner<>(item, this.valueHolder);
                    groovyScriptParser.scanner();
                }
            } else if (this.target instanceof Set<?>) {
                for (Object item : ((Set) this.target)) {
                    ObjectAnnotationScanner<T> groovyScriptParser = new ObjectAnnotationScanner<>(item, this.valueHolder);
                    groovyScriptParser.scanner();
                }
            } else {
                this.scannerFields();
            }
        }

        /**
         * 扫描字段
         */
        private void scannerFields() {
            if (this.valueHolder.hasHistory(this.target)) {
                return;
            }
            this.valueHolder.addHistory(this.target);
            ReflectionUtils.doWithFields(targetClass, field -> {
                field.setAccessible(true);
                Object value = ReflectionUtils.getField(field, target);
                Class<?> fieldTypeClass = field.getType();
                if (value != null) {
                    if (ObjectAnnotationFieldUtils.isSimpleType(fieldTypeClass)) {
                        Annotation annotation = field.getAnnotation(this.valueHolder.getAnnotationClass());
                        if (annotation != null) {
                            this.valueHolder.addFiled(field, target, (T) value);
                        }
                    } else {
                        ObjectAnnotationScanner<T> scanner = new ObjectAnnotationScanner<>(value, this.valueHolder);
                        scanner.scanner();
                    }
                }
            });
        }
    }
}
