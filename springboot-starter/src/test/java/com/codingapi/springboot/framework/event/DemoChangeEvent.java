package com.codingapi.springboot.framework.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DemoChangeEvent implements IEvent {

    private String beforeName;
    private String currentName;

}
