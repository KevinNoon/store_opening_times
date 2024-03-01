package com.optimised.repository;

import com.optimised.model.CoreTimes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoreTimesRepo extends JpaRepository<CoreTimes,Long> {
  CoreTimes findByStoreNo(Integer storeNo);
}
