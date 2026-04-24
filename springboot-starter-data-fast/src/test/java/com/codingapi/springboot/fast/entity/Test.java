package com.codingapi.springboot.fast.entity;

import com.codingapi.springboot.fast.annotation.MyIdSequence;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "t_test")
public class Test {

    @Id
    @MyIdSequence
    private Integer id;

    private String name;
}
