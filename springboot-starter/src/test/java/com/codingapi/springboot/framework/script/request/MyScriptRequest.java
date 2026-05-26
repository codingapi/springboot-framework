package com.codingapi.springboot.framework.script.request;

import com.codingapi.springboot.framework.entity.MyTest;
import com.codingapi.springboot.framework.repository.MyTestRepository;
import com.codingapi.springboot.framework.script.annotation.ScriptFunction;
import com.codingapi.springboot.framework.script.annotation.ScriptType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ScriptType(
        name = "request",
        description = "请求参数"
)
@AllArgsConstructor
public class MyScriptRequest {

    @ScriptType(name = "count", description = "总数量")
    private final int count;

    @ScriptType(name = "test", description = "test")
    private MyTest test;


    @ScriptFunction(
            name = "isSupport",
            description = "是否匹配",
            parameters = {
                    @ScriptType(name = "count", description = "描述信息")
            }
    )
    public boolean isSupport(int count) {
        return this.count == count;
    }

    public MyScriptRequest(int count) {
        this.count = count;
    }


    public void addData(MyTestRepository testRepository) {
        MyTest myTest = new MyTest();
        myTest.setName("test");
        testRepository.save(myTest);
    }

}
