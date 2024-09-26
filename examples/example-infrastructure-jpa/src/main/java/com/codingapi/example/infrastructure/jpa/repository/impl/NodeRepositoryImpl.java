package com.codingapi.example.infrastructure.jpa.repository.impl;

import com.codingapi.example.domain.Node;
import com.codingapi.example.infrastructure.jpa.jpa.JpaNodeRepository;
import com.codingapi.example.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {

    private final JpaNodeRepository jpaNodeRepository;

    @Override
    public void save(Node node) {
        jpaNodeRepository.save(node);
    }

    @Override
    public void delete(int id) {
        jpaNodeRepository.deleteById(id);
    }
}
