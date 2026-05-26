package com.codingapi.springboot.framework.script.schema;

import com.codingapi.springboot.framework.script.meta.GroovyFunction;
import com.codingapi.springboot.framework.script.meta.GroovyType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *  数据类型
 */
public class SchemaType {

    @Getter
    private List<SchemaField> fields;

    @Getter
    private List<SchemaFunction> functions;

    private final GroovySchema schema;

    public boolean isEmpty(){
        return (fields==null || fields.isEmpty()) && (functions==null || functions.isEmpty());
    }

    public SchemaType(GroovyType groovyType,GroovySchema schema) {
        this.schema = schema;
        this.addFields(groovyType.getFields());
        this.addFunctions(groovyType.getFunctions());
    }

    public void addFields(List<GroovyType> fields) {
        if(this.fields == null){
            this.fields = new ArrayList<>();
        }
        for(GroovyType field:fields){
            this.fields.add(new SchemaField(field,this.schema));
        }
    }

    public void addFunctions(List<GroovyFunction> functions) {
        if(this.functions ==null){
            this.functions = new ArrayList<>();
        }
        for(GroovyFunction function:functions){
            SchemaFunction schemaFunction = new SchemaFunction(function,this.schema);
            this.functions.add(schemaFunction);
        }
    }
}
