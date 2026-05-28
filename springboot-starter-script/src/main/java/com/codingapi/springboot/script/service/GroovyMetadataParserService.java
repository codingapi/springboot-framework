package com.codingapi.springboot.script.service;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyField;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.meta.GroovyType;

import java.util.Map;

/**
 * 脚本元数据解析对象
 */
public class GroovyMetadataParserService {

    private final GroovyScript script;

    private final GroovyMetadata groovyMetadata;

    public GroovyMetadataParserService(GroovyScript script) {
        this.script = script;
        this.groovyMetadata = new GroovyMetadata(script);
    }


    private void loadRequestTypes() {
        Map<String, Class<?>> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (Class<?> objClass : requests.values()) {
                this.groovyMetadata.buildType(objClass);
            }
        }
    }

    private void loadBindTypes() {
        Map<String, Class<?>> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (Class<?> objClass : binds.values()) {
                this.groovyMetadata.buildType(objClass);
            }
        }
    }

    private void loadReturnType() {
        Class<?> returnTypeClass = this.script.getReturnType();
        this.groovyMetadata.buildType(returnTypeClass);
    }


    private void loadRequests() {
        Map<String, Class<?>> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (String key : requests.keySet()) {
                Class<?> objClass = requests.get(key);
                String dataType = objClass.getSimpleName();
                GroovyField groovyField = new GroovyField();
                groovyField.setDataType(dataType);
                groovyField.setName(key);
                GroovyType groovyType = this.groovyMetadata.getType(dataType);
                if (groovyType != null) {
                    groovyField.setDescription(groovyType.getDescription());
                }
                this.groovyMetadata.addRequest(groovyField);
            }
        }
    }

    private void loadBinds() {
        Map<String, Class<?>> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (String key : binds.keySet()) {
                Class<?> objClass = binds.get(key);
                GroovyField groovyField = new GroovyField();
                String dataType = objClass.getSimpleName();
                groovyField.setDataType(dataType);
                groovyField.setName(key);
                GroovyType groovyType = this.groovyMetadata.getType(dataType);
                if (groovyType != null) {
                    groovyField.setDescription(groovyType.getDescription());
                }
                this.groovyMetadata.addBind(groovyField);
            }
        }
    }


    public GroovyMetadata parser() {
        this.loadRequestTypes();
        this.loadBindTypes();
        this.loadReturnType();

        this.loadRequests();
        this.loadBinds();
        return groovyMetadata;
    }

}
