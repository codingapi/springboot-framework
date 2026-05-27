package com.codingapi.springboot.script.service;

import com.codingapi.springboot.script.meta.GroovyField;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.meta.GroovyType;
import com.codingapi.springboot.script.request.GroovyBindObject;
import com.codingapi.springboot.script.request.GroovyRunningScript;

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
        this.groovyMetadata.setMainMethod(script.getMethod());
        this.groovyMetadata.setDescription(script.getDescription());
    }


    private void loadRequestTypes() {
        List<GroovyBindObject> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (GroovyBindObject request : requests) {
                if (request != null) {
                    Class<?> objClass = request.getObject().getClass();
                    this.groovyMetadata.buildType(objClass);
                }
            }
        }
    }

    private void loadBindTypes() {
        List<GroovyBindObject> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (GroovyBindObject bindObject : binds) {
                if (bindObject != null) {
                    Class<?> objClass = bindObject.getObject().getClass();
                    this.groovyMetadata.buildType(objClass);
                }
            }
        }
    }

    private void loadReturnType() {
        Class<?> returnTypeClass = this.script.getReturnType();
        this.groovyMetadata.buildType(returnTypeClass);
        this.groovyMetadata.setReturnType(returnTypeClass.getSimpleName());
    }


    private void loadRequests(){
        List<GroovyBindObject> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (GroovyBindObject request : requests) {
                if (request != null) {
                    Class<?> objClass = request.getObject().getClass();
                    String dataType = objClass.getSimpleName();
                    GroovyField groovyField = new GroovyField();
                    groovyField.setDataType(dataType);
                    groovyField.setName(request.getName());
                    GroovyType groovyType = this.groovyMetadata.getType(dataType);
                    if(groovyType!=null){
                        groovyField.setDescription(groovyType.getDescription());
                    }
                    this.groovyMetadata.addRequest(groovyField);
                }
            }
        }
    }

    private void loadBinds(){
        List<GroovyBindObject> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (GroovyBindObject bind : binds) {
                if (bind != null) {
                    Class<?> objClass = bind.getClass();
                    GroovyField groovyField = new GroovyField();
                    String dataType = objClass.getSimpleName();
                    groovyField.setDataType(dataType);
                    groovyField.setName(bind.getName());
                    GroovyType groovyType = this.groovyMetadata.getType(dataType);
                    if(groovyType!=null){
                        groovyField.setDescription(groovyType.getDescription());
                    }
                    this.groovyMetadata.addBind(groovyField);
                }
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
