package com.codingapi.example.infra.db.repository;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.repository.UserRepository;
import com.codingapi.example.infra.db.convert.UserConvertor;
import com.codingapi.example.infra.db.entity.UserEntity;
import com.codingapi.example.infra.db.jpa.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public User getUserByUsername(String username) {
        return UserConvertor.convert(userEntityRepository.getUserEntityByUsername(username), userEntityRepository);
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserConvertor.convert(user);
        entity = userEntityRepository.save(entity);
        user.setId(entity.getId());
    }

    @Override
    public User getUserById(long id) {
        return UserConvertor.convert(userEntityRepository.getUserEntityById(id), userEntityRepository);
    }

    @Override
    public void delete(long id) {
        userEntityRepository.deleteById(id);
    }

}
