package com.codingapi.springboot.authorization.repository;

import com.codingapi.springboot.authorization.entity.Depart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartRepository extends JpaRepository<Depart,Long> {

}
