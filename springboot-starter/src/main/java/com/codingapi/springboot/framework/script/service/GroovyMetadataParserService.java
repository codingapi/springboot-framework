package com.codingapi.springboot.framework.script.service;

import com.codingapi.springboot.framework.script.meta.GroovyMetadata;
import com.codingapi.springboot.framework.script.meta.GroovyType;
import com.codingapi.springboot.framework.script.request.GroovyBindObject;
import com.codingapi.springboot.framework.script.request.GroovyRunningScript;

import java.util.List;

/**
 * 脚本元数据解析对象
 */
public class GroovyMetadataParserService {

    private final GroovyRunningScript<?> script;

    private final GroovyMetadata groovyMetadata;

    public GroovyMetadataParserService(GroovyRunningScript<?> script) {
        this.script = script;
        this.groovyMetadata = new GroovyMetadata();
    }


    private void loadRequests() {
        List<Object> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (Object obj : requests) {
                if (obj != null) {
                    Class<?> objClass = obj.getClass();
                    GroovyType groovyType = this.parserGroovyObject(objClass);
                    this.groovyMetadata.addRequest(groovyType);
                }
            }
        }
    }

    private void loadBinds() {
        List<GroovyBindObject> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (GroovyBindObject bindObject : binds) {
                if (bindObject != null) {
                    String name = bindObject.getKey();
                    Class<?> objClass = bindObject.getObject().getClass();
                    GroovyType groovyType = this.parserGroovyObject(objClass);
                    groovyType.setName(name);
                    this.groovyMetadata.addBind(groovyType);
                }
            }
        }
    }

    private void loadReturnType() {
        Class<?> returnTypeClass = this.script.getReturnType();
        GroovyType groovyType = this.parserGroovyObject(returnTypeClass);
        this.groovyMetadata.setReturnType(groovyType);
    }


    private GroovyType parserGroovyObject(Class<?> clazz) {
        GroovyTypeParser parser = new GroovyTypeParser(clazz);
        return parser.parser();
    }


    public GroovyMetadata parser() {
        TempGroovyTypeCache.getInstance().clear();
        this.loadRequests();
        this.loadBinds();
        this.loadReturnType();
        return groovyMetadata;
    }

}
