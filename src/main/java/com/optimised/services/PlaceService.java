package com.optimised.services;

import com.optimised.model.Place;
import com.optimised.repository.PlaceRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {
  @Autowired
  PlaceRepo placeRepo;

  @Transactional
  public void save(Place place){
    Optional<Place> placeDB = placeRepo.findAllByPlaceId(place.getPlaceId());
    if (placeDB.isPresent()) {
      Long id = placeDB.get().getId();
      place.setId(id);
    }
    if (place.getInuse() == null) place.setInuse(true);
    placeRepo.save(place);
  }

  public Optional<Place> findByAddress(String address){
    return placeRepo.findByAddress(address);
  }

  public List<Place> findAllPlacesByNameAndAddress(String searchName, String searchAddress){
    if ((searchName == null && searchAddress.isEmpty())) {
      return placeRepo.findAll();
    } else {
      return placeRepo.filterByNameAndAddress(searchName,searchAddress);
    }

  }

  public Place findPlaceByName(String searchName){
      return placeRepo.findFirstByName(searchName);
  }


  public List<Place> findPlaceBySiteNo(Integer storeNo){
    if (placeRepo.findFirstByStoreNo(storeNo) != null){
      return placeRepo.findFirstByStoreNo(storeNo);}
    else {return placeRepo.findAll();}
  }

  public List<Place> findAllByInUseIsTrue(){
    return placeRepo.findAllByInuseIsTrue();
  }

  public List<Place> findAll() {
    return placeRepo.findAll();
  }

  public Place findByPlaceId(String placeId){
    return placeRepo.findByPlaceId(placeId);
  }

}
