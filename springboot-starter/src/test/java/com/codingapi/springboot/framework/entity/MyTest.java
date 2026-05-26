package com.codingapi.springboot.framework.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.codingapi.springboot.framework.script.annotation.ScriptField;
import com.codingapi.springboot.framework.script.annotation.ScriptType;
import lombok.Data;

@Data
@Entity
@ScriptType(description = "test")
public class MyTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ScriptField(name = "id", description = "id")
    private Long id;

    @ScriptField(name = "name", description = "name")
    private String name;
}
