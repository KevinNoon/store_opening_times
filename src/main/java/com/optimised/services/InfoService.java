package com.optimised.services;

import com.optimised.model.Info;
import com.optimised.repository.InfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InfoService {
  @Autowired
  InfoRepo infoRepo;
  public void save(Info info){
    infoRepo.save(info);
  }
  public Optional<Info> findFirst(Long id){
    return infoRepo.findById(id);
  }
}
