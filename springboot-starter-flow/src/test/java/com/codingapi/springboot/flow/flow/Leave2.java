package com.codingapi.springboot.flow.flow;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Leave2  {

    private long id;
    private String title;
    private int days;

    public Leave2(String title) {
        this(title,0);
    }

    public Leave2(String title, int days) {
        this.title = title;
        this.days = days;
    }

}
