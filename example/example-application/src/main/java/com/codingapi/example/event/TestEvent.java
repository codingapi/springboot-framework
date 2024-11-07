package com.codingapi.example.event;

import com.codingapi.springboot.framework.event.IAsyncEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestEvent implements IAsyncEvent {

    private String name;

}
