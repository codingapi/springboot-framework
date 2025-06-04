package com.codingapi.example.infra.flow.repository;

import com.codingapi.example.infra.flow.convert.FlowNodeConvertor;
import com.codingapi.example.infra.flow.convert.FlowRelationConvertor;
import com.codingapi.example.infra.flow.convert.FlowWorkConvertor;
import com.codingapi.example.infra.flow.entity.FlowWorkEntity;
import com.codingapi.example.infra.flow.jpa.FlowNodeEntityRepository;
import com.codingapi.example.infra.flow.jpa.FlowRelationEntityRepository;
import com.codingapi.example.infra.flow.jpa.FlowWorkEntityRepository;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FlowWorkRepositoryImpl implements FlowWorkRepository {

    private final FlowWorkEntityRepository flowWorkEntityRepository;
    private final FlowNodeEntityRepository flowNodeEntityRepository;
    private final FlowRelationEntityRepository flowRelationEntityRepository;
    private final FlowOperatorRepository flowOperatorRepository;

    @Override
    public FlowWork getFlowWorkById(long id) {
        FlowWorkEntity entity = flowWorkEntityRepository.getFlowWorkEntityById(id);
        if (entity == null) {
            return null;
        }

        List<FlowNode> flowNodes =
                flowNodeEntityRepository.findFlowNodeEntityByWorkId(entity.getId())
                        .stream().map(FlowNodeConvertor::convert).toList();

        List<FlowRelation> flowRelations =
                flowRelationEntityRepository.findFlowRelationEntityByWorkId(entity.getId())
                        .stream().map(item -> FlowRelationConvertor.convert(item, flowNodes)).toList();

        return new FlowWork(
                entity.getId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                flowOperatorRepository.getFlowOperatorById(entity.getCreateUser()),
                entity.getCreateTime(),
                entity.getUpdateTime(),
                entity.getEnable(),
                entity.getSkipIfSameApprover() != null && entity.getSkipIfSameApprover(),
                entity.getPostponedMax(),
                flowNodes,
                flowRelations,
                entity.getSchema()
        );
    }


    @Override
    public FlowWork getFlowWorkByCode(String code) {
        FlowWorkEntity entity = flowWorkEntityRepository.getFlowWorkEntityByCode(code);
        if (entity == null) {
            return null;
        }

        List<FlowNode> flowNodes =
                flowNodeEntityRepository.findFlowNodeEntityByWorkId(entity.getId())
                        .stream().map(FlowNodeConvertor::convert).toList();

        List<FlowRelation> flowRelations =
                flowRelationEntityRepository.findFlowRelationEntityByWorkId(entity.getId())
                        .stream().map(item -> FlowRelationConvertor.convert(item, flowNodes)).toList();

        return new FlowWork(
                entity.getId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                flowOperatorRepository.getFlowOperatorById(entity.getCreateUser()),
                entity.getCreateTime(),
                entity.getUpdateTime(),
                entity.getEnable(),
                entity.getSkipIfSameApprover() != null && entity.getSkipIfSameApprover(),
                entity.getPostponedMax(),
                flowNodes,
                flowRelations,
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

        flowNodeEntityRepository.deleteAllByWorkId(flowWork.getId());
        flowRelationEntityRepository.deleteAllByWorkId(flowWork.getId());

        if (!flowWork.getNodes().isEmpty()) {
            flowNodeEntityRepository.saveAll(flowWork.getNodes().stream().map((item) -> FlowNodeConvertor.convert(item, flowWork.getId())).toList());
        }
        if (!flowWork.getRelations().isEmpty()) {
            flowRelationEntityRepository.saveAll(flowWork.getRelations().stream().map((item) -> FlowRelationConvertor.convert(item, flowWork.getId())).toList());
        }
    }
}
