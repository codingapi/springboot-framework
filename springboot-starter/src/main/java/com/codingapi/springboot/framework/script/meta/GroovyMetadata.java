package com.codingapi.springboot.framework.script.meta;

import com.codingapi.springboot.framework.script.schema.GroovySchema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * 脚本对象元数据结构
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroovyMetadata {

    /**
     * 请求参数
     */
    private List<GroovyType> requests;

    /**
     * 绑定参数
     */
    private List<GroovyType> binds;

    /**
     * 返回类型
     */
    private GroovyType returnType;


    public void addRequest(GroovyType request){
        if(this.requests==null){
            this.requests = new ArrayList<>();
        }
        this.requests.add(request);
    }


    public void addBind(GroovyType bind){
        if(this.binds==null){
            this.binds = new ArrayList<>();
        }
        this.binds.add(bind);
    }


    public GroovySchema toSchema(){
        return new GroovySchema(this);
    }

}
