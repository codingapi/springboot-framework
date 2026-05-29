package com.codingapi.springboot.script.request;

import com.codingapi.springboot.script.annotation.ScriptParameter;
import com.codingapi.springboot.script.entity.MyTest;
import com.codingapi.springboot.script.repository.MyTestRepository;
import com.codingapi.springboot.script.annotation.ScriptField;
import com.codingapi.springboot.script.annotation.ScriptFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyScriptRequest extends BaseRequest {

    @ScriptField(name = "count", description = "总数量")
    private final int count;

    @ScriptField(name = "test", description = "test")
    private MyTest test;


    @ScriptField(name = "request",description = "request")
    private MyScriptRequest request;


    @ScriptFunction(
            name = "isSupport",
            description = "是否匹配"
    )
    public boolean isSupport(@ScriptParameter(description = "总数") int count) {
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
