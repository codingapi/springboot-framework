package com.codingapi.springboot.script.scanner;

import com.codingapi.springboot.framework.reflect.pojo.AnnotationTargetFieldResult;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GroovyScriptFieldResult {

    private final AnnotationTargetFieldResult<String> result;

    public List<String> getKeys(){
        return result.getValues();
    }

    public void update(AnnotationTargetFieldResult.Generate<String> generateKey){
        result.update(generateKey);
    }

}
