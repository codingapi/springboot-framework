package com.codingapi.springboot.framework.script.request;

import com.codingapi.springboot.framework.entity.MyTest;
import com.codingapi.springboot.framework.repository.MyTestRepository;
import com.codingapi.springboot.framework.script.annotation.ScriptField;
import com.codingapi.springboot.framework.script.annotation.ScriptFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyScriptRequest {

    @ScriptField(name = "count", description = "总数量")
    private final int count;

    @ScriptField(name = "test", description = "test")
    private MyTest test;


    @ScriptFunction(
            name = "isSupport",
            description = "是否匹配",
            parameters = {
                    @ScriptField(name = "count", description = "描述信息")
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
