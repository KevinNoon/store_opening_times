package com.optimised.controllers.api;

import com.optimised.model.Client;
import com.optimised.model.CoreTimes;
import com.optimised.services.ClientService;
import com.optimised.services.CoreTimesService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
@RequestMapping("api/v1")
public class OpeningTimes {
  @Autowired
  ClientService clientService;
  @Autowired
  CoreTimesService coreTimesService;

  @GetMapping(value = "clients")
  public List<Client> clientList(){
    return clientService.findAll();
  }

  @GetMapping(value = "clients/{id}")
  public Client client(@PathVariable Long id){
    Optional<Client> client = clientService.findById(id);
    if (client.isPresent()){
      return client.get();
    } else {
     return new Client();
    }
  }

  @GetMapping(value = "clients/{id}/corehours")
  public List<CoreTimes> coreTimesList(){
    return coreTimesService.findAll();
  }
}
