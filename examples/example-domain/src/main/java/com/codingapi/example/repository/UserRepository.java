package com.codingapi.example.repository;

import com.codingapi.example.domain.User;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserRepository {

    void save(User user);

    User getUserByUsername(String username);

    User getUserById(long id);

    List<User> findUserByIds(List<Long> ids);

    Page<User> list(SearchRequest request);
}
