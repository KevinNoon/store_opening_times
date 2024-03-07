package com.optimised.controllers.api;

import com.optimised.model.CoreTimes;
import com.optimised.model.ExceptionTime;
import com.optimised.model.Place;
import com.optimised.services.CoreTimesService;
import com.optimised.services.ExceptionTimeService;
import com.optimised.services.PlaceService;
import com.optimised.services.settings.SettingsService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
@RequestMapping("api/v1")
public class OpeningTimes {

  @Autowired
  CoreTimesService coreTimesService;
  @Autowired
  ExceptionTimeService exceptionTimeService;
  @Autowired
  SettingsService settingsService;
  @Autowired
  PlaceService placeService;

  @GetMapping(value = "/corehours")
  public List<CoreTimes> coreTimesList(){
    return coreTimesService.findAll();
  }

  @GetMapping(value = "/exceptions")
  public List<ExceptionTime> exceptions() {
    return exceptionTimeService.findAll();
  }

  @GetMapping(value = "/exceptions/changed")
  public List<ExceptionTime> exceptionTimes(){
    List<ExceptionTime> exceptionTimes = exceptionTimeService.findByChanged();
    if (settingsService.getSettings().getChangeFlagReset().equals("API_OPEN")){
      for (ExceptionTime et:exceptionTimes
      ) {
        exceptionTimeService.setChangedFalse(et);
      }
    }
    return exceptionTimeService.findByChanged();
  }

  @GetMapping(value = "/places")
  public List<Place> getPlaces(){
    return placeService.findAll();
  }

  @GetMapping(value = "/places/{id}")
  public Place getPlace(@PathVariable Long id){
    Optional<Place> place = placeService.findByPlaceId(id);
    if (!place.isPresent()){
      return new Place();
    }
    return place.get();
  }

  @PostMapping(value = "/exceptions/changed/{id}")
  public ExceptionTime resetChanged(@PathVariable Long id){
    Optional<ExceptionTime> exceptionTime= exceptionTimeService.findById(id);
    if (exceptionTime.isPresent()){
      exceptionTimeService.setChangedFalse(exceptionTime.get());
      return exceptionTime.get();
    }
    return new ExceptionTime();
  }
}
