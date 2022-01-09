package com.codingapi.springboot.example.domain;

import com.codingapi.springboot.example.domain.event.DemoNameChangeEvent;
import com.codingapi.springboot.framework.persistence.IPersistence;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@ToString
public class Demo implements IPersistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public Demo(String name) {
        this.name = name;
    }

    public void changeName(String name){
        String oldName = this.name;
        this.name = name;

        EventPusher.push(new DemoNameChangeEvent(oldName,name));
    }

}
