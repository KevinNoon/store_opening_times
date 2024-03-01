package com.optimised.services;

import com.optimised.model.User;
import com.optimised.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }
    public List<User> findAllUsers(){
        return repository.findAll();
    }

    public List<User> findAllErrorEmails(){
        return repository.findAllByEmailErrorIsTrue();
    }

    public List<User> findAllCsvEmails(){
        return repository.findAllByEmailCsvIsTrue();
    }

    public int count() {
        return (int) repository.count();
    }

}
