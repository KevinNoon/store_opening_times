package com.optimised.repository;

import com.optimised.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client,Long> {
  Client findByName(String name);
}
