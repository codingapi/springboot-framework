package com.codingapi.springboot.fast.repository;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.jpa.FastRepository;
import com.codingapi.springboot.fast.jpa.SortRepository;

public interface DemoRepository extends FastRepository<Demo,Integer>, SortRepository<Demo,Integer> {

}
