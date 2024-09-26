package com.codingapi.example.service;

import com.codingapi.example.domain.Node;
import com.codingapi.example.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public void save(Node node){
        nodeRepository.save(node);
    }

    public void delete(int id){
        nodeRepository.delete(id);
    }

}
