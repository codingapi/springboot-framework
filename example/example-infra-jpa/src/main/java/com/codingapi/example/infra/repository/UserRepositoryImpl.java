package com.codingapi.example.infra.repository;

import com.codingapi.example.domain.User;
import com.codingapi.example.infra.convert.UserConvertor;
import com.codingapi.example.infra.entity.UserEntity;
import com.codingapi.example.infra.jpa.UserEntityRepository;
import com.codingapi.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public User getUserByUsername(String username) {
        return UserConvertor.convert(userEntityRepository.getByUsername(username));
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserConvertor.convert(user);
        entity = userEntityRepository.save(entity);
        user.setId(entity.getId());
    }
}
