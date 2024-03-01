package com.optimised.services;

import com.optimised.model.StoreSystem;
import com.optimised.repository.StoreSystemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreSystemService {
  @Autowired
  StoreSystemRepo storeSystemRepo;

  public List<StoreSystem> findAll(){
    return storeSystemRepo.findAll();
  }

  public List<String> findAllSystems() {
    List<StoreSystem> storeSystems = storeSystemRepo.findAll();
    List<String> allSystems = new ArrayList<>();
    for (StoreSystem s:storeSystems
         ) {
      allSystems.add(s.getName());
    }
    return  allSystems;
  }

  public void save(StoreSystem storeSystem){
    StoreSystem storeSystemDB = storeSystemRepo.findByName(storeSystem.getName());
    if (storeSystemDB != null){
      storeSystem.setId(storeSystemDB.getId());
    }
    storeSystemRepo.save(storeSystem);
  }
  public void delete(Long id){
    Optional<StoreSystem> storeSystem = storeSystemRepo.findById(id);
    {
      if (storeSystem.isPresent()){
        storeSystemRepo.delete(storeSystem.get());
      }
    }
  }
}
