package com.codingapi.springboot.framework.script.schema;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.script.meta.GroovyMetadata;
import com.codingapi.springboot.framework.script.meta.GroovyType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  脚本提示信息
 */
public class GroovySchema {

    @Getter
    private final Map<String, SchemaType> types;

    @Getter
    private final List<SchemaField> requests;

    @Getter
    private final List<SchemaField> binds;

    @Getter
    private final String returnType;

    private transient final GroovyMetadata metadata;

    public GroovySchema(GroovyMetadata metadata) {
        this.metadata = metadata;
        this.types = new HashMap<>();
        this.requests = new ArrayList<>();
        this.binds = new ArrayList<>();

        this.loadRequestTypes();
        this.loadBindTypes();
        this.loadReturnType();

        this.loadRequests();
        this.loadBinds();

        this.returnType = metadata.getReturnType().getDataClassName();
    }


    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    private void loadRequests() {
        if (this.metadata.getRequests() != null) {
            for (GroovyType groovyType : this.metadata.getRequests()) {
                SchemaField schemaField = new SchemaField(groovyType);
                this.requests.add(schemaField);
            }
        }
    }


    private void loadBinds() {
        if (this.metadata.getBinds() != null) {
            for (GroovyType groovyType : this.metadata.getBinds()) {
                SchemaField schemaField = new SchemaField(groovyType);
                this.binds.add(schemaField);
            }
        }
    }

    private void loadRequestTypes() {
        if (this.metadata.getRequests() != null) {
            for (GroovyType groovyType : this.metadata.getRequests()) {
                this.loadSchemaType(groovyType);
            }
        }
    }

    public void loadSchemaType(GroovyType groovyType) {
        String key = groovyType.getDataClassName();
        if (this.types.get(key) == null) {
            SchemaType schemaType = new SchemaType(groovyType, this);
            this.types.put(groovyType.getDataClassName(), schemaType);
        }
    }

    private void loadBindTypes() {
        if (this.metadata.getBinds() != null) {
            for (GroovyType groovyType : this.metadata.getBinds()) {
                this.loadSchemaType(groovyType);
            }
        }
    }

    private void loadReturnType() {
        GroovyType groovyType = this.metadata.getReturnType();
        if (groovyType != null) {
            this.loadSchemaType(groovyType);
        }
    }


}
