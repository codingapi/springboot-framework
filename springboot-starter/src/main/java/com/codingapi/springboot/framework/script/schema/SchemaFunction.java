package com.codingapi.springboot.framework.script.schema;

import com.codingapi.springboot.framework.script.meta.GroovyFunction;
import com.codingapi.springboot.framework.script.meta.GroovyType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 函数定义
 */
@Setter
public class SchemaFunction {

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String returnType;
    @Getter
    private List<SchemaField> parameters;

    public SchemaFunction(GroovyFunction function,GroovySchema schema) {
        this.name = function.getName();
        this.description = function.getDescription();
        this.returnType = function.getReturnType().getDataClassName();
        this.parameters = new ArrayList<>();

        for(GroovyType request: function.getParameters()){
            this.parameters.add(new SchemaField(request,schema));
        }

        if(schema!=null){
            schema.loadSchemaType(function.getReturnType());
        }
    }

}
