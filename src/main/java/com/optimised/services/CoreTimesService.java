package com.optimised.services;

import com.optimised.model.CoreTimes;
import com.optimised.repository.CoreTimesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoreTimesService {
  @Autowired
  CoreTimesRepo coreTimesRepo;

  public CoreTimes findByStoreNo(Integer storeNo){
    return coreTimesRepo.findByStoreNo(storeNo);
  }
  public void save(CoreTimes coreTimes){
    CoreTimes coreTimesDB = coreTimesRepo.findByStoreNo(coreTimes.getStoreNo());
    if (coreTimesDB != null){
      coreTimes.setId(coreTimesDB.getId());
    }
    coreTimesRepo.save(coreTimes);
  }
  public void delete(Long id){
    Optional<CoreTimes> coreTimes = coreTimesRepo.findById(id);
    if (coreTimes.isPresent()){
      coreTimesRepo.delete(coreTimes.get());
    }
  }

  public List<CoreTimes> findAll() {
    return coreTimesRepo.findAll();
  }
}
