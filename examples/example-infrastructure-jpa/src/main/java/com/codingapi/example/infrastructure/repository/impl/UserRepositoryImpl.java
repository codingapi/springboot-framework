package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.domain.User;
import com.codingapi.example.infrastructure.convert.UserConvertor;
import com.codingapi.example.infrastructure.entity.UserEntity;
import com.codingapi.example.infrastructure.jpa.UserEntityRepository;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public void save(User user) {
        UserEntity entity = UserConvertor.convert(user);
        entity = userEntityRepository.save(entity);
        user.setId(entity.getId());
    }

    @Override
    public User getUserByUsername(String username) {
        return UserConvertor.convert(userEntityRepository.getUserEntityByUsername(username));
    }

    @Override
    public User getUserById(long id) {
        return UserConvertor.convert(userEntityRepository.getUserEntityById(id));
    }

    @Override
    public List<User> findUserByIds(List<Long> ids) {
        return userEntityRepository.findUserEntityByIdIn(ids).stream().map(UserConvertor::convert).toList();
    }

    @Override
    public Page<User> list(SearchRequest request) {
        Page<UserEntity> userEntityPage =  userEntityRepository.searchRequest(request);
        return userEntityPage.map(UserConvertor::convert);
    }
}
