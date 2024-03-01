package com.optimised.services;

import com.optimised.model.County;
import com.optimised.repository.CountyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CountyService {
  @Autowired
  CountyRepo countyRepo;

  public List<County>  findAll(){
    return  countyRepo.findAll();
  }

  public void save(County county){
    County countyDB = countyRepo.findByName(county.getName());
    if (countyDB != null){
      county.setId(countyDB.getId());
    }
    countyRepo.save(county);
  }

  public void delete(Long id){
    Optional<County> county = countyRepo.findById(id);
    {
      if (county.isPresent()){
        countyRepo.delete(county.get());
      }
    }
  }
}
