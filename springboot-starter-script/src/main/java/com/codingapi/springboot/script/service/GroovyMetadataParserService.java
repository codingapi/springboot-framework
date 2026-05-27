package com.codingapi.springboot.script.service;

import com.codingapi.springboot.script.meta.GroovyField;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.meta.GroovyType;
import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.bind.ClassBinder;

import java.util.List;

/**
 * 脚本元数据解析对象
 */
public class GroovyMetadataParserService {

    private final GroovyScript script;

    private final GroovyMetadata groovyMetadata;

    public GroovyMetadataParserService(GroovyScript script) {
        this.script = script;
        this.groovyMetadata = new GroovyMetadata();
        this.groovyMetadata.setMainMethod(script.getMethod());
        this.groovyMetadata.setDescription(script.getDescription());
    }


    private void loadRequestTypes() {
        List<ClassBinder> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (ClassBinder request : requests) {
                if (request != null) {
                    Class<?> objClass = request.getClazz();
                    this.groovyMetadata.buildType(objClass);
                }
            }
        }
    }

    private void loadBindTypes() {
        List<ClassBinder> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (ClassBinder bindObject : binds) {
                if (bindObject != null) {
                    Class<?> objClass = bindObject.getClazz();
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
        List<ClassBinder> requests = script.getRequests();
        if (requests != null && !requests.isEmpty()) {
            for (ClassBinder request : requests) {
                if (request != null) {
                    Class<?> objClass = request.getClazz();
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
        List<ClassBinder> binds = script.getBinds();
        if (binds != null && !binds.isEmpty()) {
            for (ClassBinder bind : binds) {
                if (bind != null) {
                    Class<?> objClass = bind.getClazz();
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
