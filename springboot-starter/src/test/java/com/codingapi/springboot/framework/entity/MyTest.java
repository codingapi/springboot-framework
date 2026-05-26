package com.codingapi.springboot.framework.entity;

import com.codingapi.springboot.framework.script.annotation.ScriptField;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MyTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ScriptField(name = "id", description = "id")
    private Long id;

    @ScriptField(name = "name", description = "name")
    private String name;
}
