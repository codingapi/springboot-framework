package com.codingapi.springboot.example.domain.event;

import com.codingapi.springboot.framework.event.IAsyncEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
@AllArgsConstructor
public class DemoNameChangeEvent implements IAsyncEvent {

    private String oldName;

    private String currentName;

}
