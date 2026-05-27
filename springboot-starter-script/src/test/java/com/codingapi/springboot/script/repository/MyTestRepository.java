package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.entity.MyTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyTestRepository extends JpaRepository<MyTest,Long> {

}
