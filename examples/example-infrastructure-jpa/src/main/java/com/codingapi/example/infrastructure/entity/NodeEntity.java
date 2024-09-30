package com.codingapi.example.infrastructure.entity;

import com.codingapi.example.domain.Node;
import com.codingapi.example.em.State;
import com.codingapi.springboot.framework.serializable.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class NodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "text")
    private String url;

    @JsonSerialize(using = EnumSerializer.class)
    private State state;

}
