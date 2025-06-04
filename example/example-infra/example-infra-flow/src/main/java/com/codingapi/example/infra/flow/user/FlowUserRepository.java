package com.codingapi.example.infra.flow.user;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.repository.UserRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class FlowUserRepository implements FlowOperatorRepository {

    private final UserRepository userRepository;

    @Override
    public List<? extends IFlowOperator> findByIds(List<Long> ids) {
        return ids.stream().map(id->{
            User user = userRepository.getUserById(id);
            return new FlowUser(user);
        }).collect(Collectors.toList());
    }

    @Override
    public IFlowOperator getFlowOperatorById(long id) {
        return new FlowUser(userRepository.getUserById(id));
    }


    public IFlowOperator getUserByUsername(String username) {
        return new FlowUser(userRepository.getUserByUsername(username));
    }
}
