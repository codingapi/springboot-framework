package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.domain.Node;
import com.codingapi.example.infrastructure.convert.NodeConvertor;
import com.codingapi.example.infrastructure.entity.NodeEntity;
import com.codingapi.example.infrastructure.jpa.NodeEntityRepository;
import com.codingapi.example.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {

    private final NodeEntityRepository nodeEntityRepository;

    @Override
    public void save(Node node) {
        NodeEntity entity = NodeConvertor.convert(node);
        entity = nodeEntityRepository.save(entity);
        node.setId(entity.getId());
    }

    @Override
    public void delete(long id) {
        nodeEntityRepository.deleteById(id);
    }
}
