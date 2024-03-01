package com.optimised.repository;


import com.optimised.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    List<User> findAllByEmailErrorIsTrue();

    List<User> findAllByEmailCsvIsTrue();
}
