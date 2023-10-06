package com.codingapi.springboot.fast.repository;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.query.FastRepository;
import com.codingapi.springboot.fast.sort.SortRepository;

public interface DemoRepository extends FastRepository<Demo,Integer>, SortRepository<Demo,Integer> {

}
