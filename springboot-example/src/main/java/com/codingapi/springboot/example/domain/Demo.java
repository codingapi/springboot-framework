package com.codingapi.springboot.example.domain;

import com.codingapi.springboot.example.event.DemoNameChangeEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.leaf.LeafIdGenerate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity(name = "t_demo")
public class Demo  implements LeafIdGenerate {

    @Id
    private Integer id;

    private String name;

    public Demo(String name) {
        this.id = generateIntId();
        this.name = name;
    }

    public void changeName(String name){
        String oldName = this.name;
        this.name = name;

        EventPusher.push(new DemoNameChangeEvent(oldName,name));
    }

}
