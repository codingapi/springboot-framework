package com.codingapi.springboot.fast.entity;

import com.codingapi.springboot.fast.jpa.ISort;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.ToString;

@Setter
@Getter
@Entity
@Table(name = "t_demo")
@ToString
public class Demo implements ISort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer sort;
}
