package com.optimised.services;

import com.optimised.model.ExceptionTime;
import com.optimised.repository.ExceptionTimeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExceptionTimeService {
  @Autowired
  ExceptionTimeRepo execptionTimeRepo;

  public List<ExceptionTime> findByChangeDateAfter(){
    return execptionTimeRepo.findByChangeDateAfter(LocalDate.now().minusDays(1l));
  }
  public ExceptionTime findByStoreNo(Integer storeNo){
    return execptionTimeRepo.findByStoreNo(storeNo);
  }

  public Optional<ExceptionTime> findById(Long id){
    return execptionTimeRepo.findById(id);

  }

  public void save(ExceptionTime execptionTime){
    ExceptionTime exceptionTimeDB = execptionTimeRepo.findByStoreNoAndChangeDate(execptionTime.getStoreNo(),execptionTime.getChangeDate());
    if (exceptionTimeDB != null){
      execptionTime.setId(exceptionTimeDB.getId());
      if ((!execptionTime.getOpen().equals(exceptionTimeDB.getOpen())) || (!execptionTime.getClose().equals(exceptionTimeDB.getClose()))){
        execptionTime.setChanged(true);
      }  else {
        execptionTime.setChanged(exceptionTimeDB.getChanged());
      }
    } else {
      execptionTime.setChanged(true);
    }
    execptionTimeRepo.save(execptionTime);
  }

  public void setChangedFalse(ExceptionTime exceptionTime){
    if (execptionTimeRepo.findByStoreNoAndChangeDate(exceptionTime.getStoreNo(),exceptionTime.getChangeDate())!= null){
      exceptionTime.setChanged(false);
      execptionTimeRepo.save(exceptionTime);
    }
  }

  public void delete(Long id){
    Optional<ExceptionTime> changedTimes = execptionTimeRepo.findById(id);
    if (changedTimes.isPresent()){
      execptionTimeRepo.delete(changedTimes.get());
    }
  }

  public List<ExceptionTime> findAll() {
    return execptionTimeRepo.findAll();
  }

  public List<ExceptionTime> findByChanged(){
    return execptionTimeRepo.findByChangedTrue();
  }
}
