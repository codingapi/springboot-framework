package com.codingapi.springboot.framework.entity;

import com.codingapi.springboot.framework.script.annotation.ScriptType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@ScriptType
public class MyTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ScriptType(name = "id", description = "id")
    private Long id;

    @ScriptType(name = "name", description = "name")
    private String name;
}
