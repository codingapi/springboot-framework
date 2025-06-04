package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.bind.IBindData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlowMerge {

    private final FlowRecord flowRecord;
    private final IBindData bindData;

}
