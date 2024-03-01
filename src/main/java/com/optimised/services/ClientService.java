package com.optimised.services;

import com.optimised.model.Client;
import com.optimised.repository.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
  @Autowired
  ClientRepo clientRepo;

  public List<Client> findAll() {
    return clientRepo.findAll();
  }

  public Optional<Client> findById(Long id){
    return clientRepo.findById(id);
  }

  public void save(Client client){
    Client clientDB = clientRepo.findByName(client.getName());
    if (clientDB != null){
      client.setId(clientDB.getId());
    }
    clientRepo.save(client);
  }
  public void delete(Long id){
    Optional<Client> client = clientRepo.findById(id);
    {
      if (client.isPresent()){
        clientRepo.delete(client.get());
      }
    }
  }
}
