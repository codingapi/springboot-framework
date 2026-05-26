package com.codingapi.springboot.framework.entity;

import com.codingapi.springboot.framework.script.annotation.ScriptType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@ScriptType(dataType = MyTest.class)
public class MyTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ScriptType(name = "id", description = "id", dataType = Long.class)
    private Long id;

    @ScriptType(name = "name", description = "name", dataType = String.class)
    private String name;
}
