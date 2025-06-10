package com.codingapi.springboot.flow.flow;

import com.codingapi.springboot.flow.bind.IBindData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Leave implements IBindData {

    private long id;
    private String title;
    private int days;

    public Leave(String title) {
        this(title,0);
    }

    public Leave(String title, int days) {
        this.title = title;
        this.days = days;
    }

}
