package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.entity.MyTest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyTestAsyncEvent implements IAsyncEvent{

    private final MyTest myTest;
}
