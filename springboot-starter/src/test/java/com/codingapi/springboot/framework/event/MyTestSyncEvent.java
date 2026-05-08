package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.entity.MyTest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyTestSyncEvent implements ISyncEvent{

    private final MyTest myTest;
}
