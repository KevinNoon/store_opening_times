package com.optimised.repository;

import com.optimised.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepo extends JpaRepository<Logs,Long> {
  @Query("select l from Logs l " +
      "where lower(l.level) like lower(concat('%', :searchLevel, '%')) " +
      "and lower(l.message) like lower(concat('%', :searchMessage, '%'))")
  List<Logs> filterByLevelAndMessage(@Param("searchLevel") String searchLevel, @Param("searchMessage") String searchMessage);

  @Query ("Select l from Logs l where l.id > :id and lower(l.level) like 'error'" )
  List<Logs> findErrorLogs(@Param("id") Long Id);
}
