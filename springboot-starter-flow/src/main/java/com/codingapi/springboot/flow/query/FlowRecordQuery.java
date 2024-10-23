package com.codingapi.springboot.flow.query;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.List;

public interface FlowRecordQuery {

    List<FlowRecord> findAll();

}
