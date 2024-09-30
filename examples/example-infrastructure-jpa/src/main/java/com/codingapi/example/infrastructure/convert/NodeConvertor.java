package com.codingapi.example.infrastructure.convert;

import com.codingapi.example.domain.Node;
import com.codingapi.example.infrastructure.entity.NodeEntity;
import com.codingapi.springboot.fast.manager.EntityManagerContent;

public class NodeConvertor {

    public static Node convert(NodeEntity entity) {
        if (entity == null) {
            return null;
        }

        Node node = new Node();
        node.setId(entity.getId());
        node.setName(entity.getName());
        node.setUrl(entity.getUrl());
        node.setState(entity.getState());

        EntityManagerContent.getInstance().detach(entity);
        return node;
    }

    public static NodeEntity convert(Node node){
        if(node==null){
            return null;
        }
        NodeEntity entity = new NodeEntity();
        entity.setId(node.getId());
        entity.setName(node.getName());
        entity.setUrl(node.getUrl());
        entity.setState(node.getState());
        return entity;
    }
}
