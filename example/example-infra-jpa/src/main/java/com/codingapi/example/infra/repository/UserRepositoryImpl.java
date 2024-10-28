package com.codingapi.example.infra.repository;

import com.codingapi.example.domain.User;
import com.codingapi.example.infra.convert.UserConvertor;
import com.codingapi.example.infra.entity.UserEntity;
import com.codingapi.example.infra.jpa.UserEntityRepository;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository, FlowOperatorRepository {

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
    public List<? extends IFlowOperator> findByIds(List<Long> ids) {
        return userEntityRepository.findUserEntityByIdIn(ids).stream().map(item -> UserConvertor.convert(item, userEntityRepository)).toList();
    }

    @Override
    public void delete(long id) {
        userEntityRepository.deleteById(id);
    }

    @Override
    public IFlowOperator getFlowOperatorById(long id) {
        return this.getUserById(id);
    }
}
