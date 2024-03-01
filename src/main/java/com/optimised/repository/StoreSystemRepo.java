package com.optimised.repository;

import com.optimised.model.StoreSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreSystemRepo extends JpaRepository<StoreSystem,Long> {
  StoreSystem findByName(String name);
}
