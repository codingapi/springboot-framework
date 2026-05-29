package com.codingapi.springboot.framework.reflect.pojo;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 注解目标字段对象集合
 *
 * @param <T> 注解字段类型
 */
@AllArgsConstructor
public class AnnotationTargetFieldResult<T> {

    private final List<AnnotationTargetField<T>> fields;

    public List<T> getValues() {
        return fields.stream().map(AnnotationTargetField::getValue).distinct().collect(Collectors.toList());
    }

    public void fetch(Consumer<AnnotationTargetField<T>> consumer) {
        this.fields.forEach(consumer);
    }

    public void update(Generate<T> generateValue) {
        this.fields.forEach(tAnnotationTargetField -> {
            tAnnotationTargetField.update(generateValue.generate(tAnnotationTargetField.getValue()));
        });
    }


    public interface Generate<T>{

        T generate(T value);

    }

}
