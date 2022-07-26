package com.codingapi.springboot.leaf.segment.dao;


import com.codingapi.springboot.leaf.segment.model.LeafAlloc;

import java.util.List;

public interface IDAllocDao {

     List<LeafAlloc> getAllLeafAllocs();

     LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

     LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

     List<String> getAllTags();

     boolean save(LeafAlloc leafInfo);
}
