package com.codingapi.springboot.framework.repository;

import com.codingapi.springboot.framework.entity.MyTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTestRepository extends JpaRepository<MyTest,Long> {

}
