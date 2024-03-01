package com.optimised.repository;


import com.optimised.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepo extends JpaRepository<County,Long> {
  County findByName(String name);
}
