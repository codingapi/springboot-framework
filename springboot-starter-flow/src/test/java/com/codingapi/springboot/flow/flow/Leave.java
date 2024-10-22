package com.codingapi.springboot.flow.flow;

import com.codingapi.springboot.flow.bind.IBindData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Leave implements IBindData {

    private long id;
    private String title;

    public Leave(String title) {
        this.title = title;
    }

    @Override
    public long getId() {
        return id;
    }
}
