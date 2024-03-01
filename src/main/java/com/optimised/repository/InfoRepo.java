package com.optimised.repository;

import com.optimised.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepo extends JpaRepository<Info,Long> {
}
