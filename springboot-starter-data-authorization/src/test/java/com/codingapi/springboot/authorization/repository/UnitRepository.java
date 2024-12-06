package com.codingapi.springboot.authorization.repository;

import com.codingapi.springboot.authorization.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit,Long> {

}
