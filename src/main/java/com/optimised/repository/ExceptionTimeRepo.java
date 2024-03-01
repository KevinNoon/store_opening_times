package com.optimised.repository;

import com.optimised.model.ExceptionTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExceptionTimeRepo extends JpaRepository<ExceptionTime,Long> {
  ExceptionTime findByStoreNo(Integer storeNo);
  ExceptionTime findByStoreNoAndChangeDate(Integer storeNo, LocalDate changeTime);
  List<ExceptionTime> findByChangeDateAfter(LocalDate now);
  List<ExceptionTime> findByChangedTrue();
}
