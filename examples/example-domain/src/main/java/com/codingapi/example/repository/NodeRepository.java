package com.codingapi.example.repository;

import com.codingapi.example.domain.Node;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface NodeRepository {

    void save(Node node);

    void delete(int id);

}
