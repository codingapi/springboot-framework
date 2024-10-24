package com.codingapi.example.repository;

import com.codingapi.example.convert.FlowWorkConvertor;
import com.codingapi.example.entity.FlowWorkEntity;
import com.codingapi.example.jpa.FlowWorkEntityRepository;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FlowWorkRepositoryImpl implements FlowWorkRepository {

    private final FlowWorkEntityRepository flowWorkEntityRepository;
    private final UserRepository userRepository;

    @Override
    public FlowWork getFlowWorkById(long id) {
        FlowWorkEntity entity = flowWorkEntityRepository.getFlowWorkEntityById(id);
        if (entity == null) {
            return null;
        }
        return new FlowWork(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                userRepository.getUserById(entity.getCreateUser()),
                entity.getCreateTime(),
                entity.getUpdateTime(),
                entity.isEnable(),
                entity.getPostponedMax(),
                null,
                null,
                entity.getSchema()
        );
    }


    @Override
    public void delete(long id) {
        flowWorkEntityRepository.deleteById(id);
    }

    @Override
    public void save(FlowWork flowWork) {
        FlowWorkEntity entity = FlowWorkConvertor.convert(flowWork);
        entity = flowWorkEntityRepository.save(entity);
        flowWork.setId(entity.getId());
    }
}
