package com.codingapi.springboot.framework.script.schema;

import com.codingapi.springboot.framework.script.meta.GroovyType;
import lombok.Getter;
import lombok.Setter;

/**
 * 字段定义
 */
@Setter
public class SchemaField {

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String dataType;

    public SchemaField(GroovyType filed) {
        this(filed,null);
    }

    public SchemaField(GroovyType filed,GroovySchema schema) {
        this.name = filed.getName();
        this.description = filed.getDescription();
        this.dataType = filed.getDataClassName();

        if(schema!=null){
            schema.loadSchemaType(filed);
        }
    }

}
