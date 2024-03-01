package com.optimised.services;

import com.optimised.model.Logs;
import com.optimised.repository.LogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
  @Autowired
  LogRepo logRepo;
  public List<Logs> findAll(){
    return logRepo.findAll();
  }

  public List<Logs> findErrorLogs(Long lastRecordNo){
    return logRepo.findErrorLogs(lastRecordNo);
  }

  public List<Logs> findByLevelAndMessage(String searchLevel, String searchMessage){
    if ((searchLevel.isEmpty() && searchMessage.isEmpty())) {
      return logRepo.findAll();
    } else {
      return logRepo.filterByLevelAndMessage(searchLevel,searchMessage);
    }
  }
}
