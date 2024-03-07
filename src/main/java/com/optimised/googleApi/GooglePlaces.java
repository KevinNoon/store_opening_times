package com.optimised.googleApi;

import com.optimised.model.*;
import com.optimised.model.settings.Setting;
import com.optimised.services.*;
import com.optimised.services.settings.SettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

@Component
@Service
public class GooglePlaces {
  private static Logger log;

  @Autowired
  SettingsService settingsService;
  @Autowired
  PlaceService placeService;
  @Autowired
  CountyService countyService;
  @Autowired
  InfoService infoService;
  @Autowired
  CoreTimesService coreTimesService;
  @Autowired
  ExceptionTimeService exceptionTimeService;

  GoogleService googleService = new GoogleService();

  //ToDo have more logs
  //ToDo email errors
  @Scheduled(cron = "0 0/1 * * * *") //""${cronTimes}")
  private void fetchOpeningTimes() {
    log = LogManager.getLogger(GooglePlaces.class);
    LocalTime getDataTime = settingsService.getSettings().getUpdateTime();
    LocalTime currentTime = LocalTime.now();
    Boolean getTimes = false;
    if (settingsService.getSettings().getEnableAutoUpdate()) {
      getTimes = (getDataTime.getHour() == currentTime.getHour()) && (getDataTime.getMinute() == currentTime.getMinute());
    }
    if (getTimes) {
      GetDetails getDetails = new GetDetails();
      getDetails.run();
    }
  }

  public void runGetPlace() {
    GetPlace getPlace = new GetPlace();
    getPlace.run();
  }

  public void runGetPlaceDetails() {
    GetDetails getDetails = new GetDetails();
    getDetails.run();
  }

  public class GetDetails extends Thread {
    public void run() {
      log = LogManager.getLogger(GooglePlaces.class);
      Boolean hoursChanged = false;
      List<Place> places;
      places = placeService.findAll();
      Setting setting = settingsService.getSettings();
      for (Place place : places) {
        StringBuilder result = GoogleService.getPlaceDetails(setting.getApiKey(), place.getPlaceId());
        //Check if place found if not check the Place_ID
        // System.out.println(GoogleService.getStatus(result) + " " + setting.getApiKey());
        if (GoogleService.getStatus(result).equals("OK")) {
          Place placeResult = GoogleService.setPlaceDetails(result);
          if (savePlaceDetails(placeResult)) {
            hoursChanged = true;
          }
        } else {
          if (GoogleService.getStatus(result).equals("INVALID_REQUEST")) {
            if (place.getAddress() != null && !place.getAddress().isEmpty()) {
              ArrayList<Place> p = GoogleService.setPlace(googleService.getPlace(setting.getApiKey(), place.getAddress()));
              String placeAddressOld = place.getAddress();
              for (int i = 0; i < p.size(); i++) {
                String placeAddressNew = p.get(i).getAddress();
                if (placeAddressOld.substring(0, placeAddressOld.indexOf(",")).
                    equals(placeAddressNew.substring(0, placeAddressNew.indexOf(",")))) {
                  place.setPlaceId(p.get(i).getPlaceId());
                  if (savePlaceDetails(place)) {
                    hoursChanged = true;
                  }
                  ;
                  break;
                }
              }
            }
          }
        }
      }
      Info info = new Info();
      info.setLastUpdateTime(LocalDateTime.now().toString());
      info.setId(1l);
      infoService.save(info);
    }
  }

  public Boolean savePlaceDetails(Place place) {
    log = LogManager.getLogger(GooglePlaces.class);
    Boolean changedHours = false;

//    Place placeDB = placeService.findByPlaceId(place.getPlaceId());
    // Set<OpeningHours> openingHours = place.getOpeningHours();
    //Check in time is different from core hours
    //Get the core hours for the store
    //Get the store number from the place

    Place placeStoreNo = placeService.findPlaceByName(place.getName());
    Integer storeNo = 0;
    //Check if the place exists if not create a new one
    //Set the inuse to false as it will not have a site number
    System.out.println("Place store No " + placeStoreNo);
    if (placeStoreNo == null) {
      place.setInuse(false);
      placeService.save(place);
      log.warn("New site added");
      //Place does not exist therefore break
      return false;
    } else {
      storeNo = placeStoreNo.getStoreNo();
    }

    if (storeNo != null) {
      //We got the coreTime now compare with the Google times
      CoreTimes coreTimes = coreTimesService.findByStoreNo(storeNo);
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getMonOpen(), place.getMonClose(),
          coreTimes.getMonOpen(), coreTimes.getMonClose(), 1); //Monday = DayOfWeek 1
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getTueOpen(), place.getTueClose(),
          coreTimes.getTueOpen(), coreTimes.getTueClose(), 2); //Tuesday = DayOfWeek 2
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getWedOpen(), place.getWedClose(),
          coreTimes.getWedOpen(), coreTimes.getWedClose(), 3); //Wednesday = DayOfWeek 3
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getThuOpen(), place.getThuClose(),
          coreTimes.getThuOpen(), coreTimes.getThuClose(), 4); //Thursday = DayOfWeek 4
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getFriOpen(), place.getFriClose(),
          coreTimes.getFriOpen(), coreTimes.getFriClose(), 5); //Friday = DayOfWeek 5
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getSatOpen(), place.getSatClose(),
          coreTimes.getSatOpen(), coreTimes.getSatClose(), 6); //Saturday = DayOfWeek 6
      CheckForExceptionTime(place.getName(), place.getStoreNo(), place.getSunOpen(), place.getSunClose(),
          coreTimes.getSunOpen(), coreTimes.getSunClose(), 7); //Sunday = DayOfWeek 7
    }

    return (changedHours);
  }

  private Void CheckForExceptionTime(String name, Integer storeNo, LocalTime pOpen, LocalTime pClose,
                                     LocalTime cOpen, LocalTime cClose, Integer dow) {
    Boolean hoursChanged = OpenTimeCheck(pOpen, pClose, cOpen, cClose);
    //If changed then add exception
    if (hoursChanged) {
      //Get the exception date
      LocalDate ld = calExceptionDate(dow);
      //Create the exception
      ExceptionTime et = new ExceptionTime();
      et.setChangeDate(ld);
      et.setOpen(pOpen);
      et.setClose(pClose);
      et.setStoreNo(storeNo);
      et.setStoreName(name);
      //Save the exception
      exceptionTimeService.save(et);
    }

    return null;
  }

  private boolean OpenTimeCheck(LocalTime coreOt, LocalTime coreCt, LocalTime webOt, LocalTime webCt) {
    return !(coreOt.equals(webOt) && coreCt.equals(webCt));
  }

  static LocalDate calExceptionDate(Integer exceptionDOW) {
    Integer nowDOW = LocalDate.now().getDayOfWeek().getValue();
    Integer diff = exceptionDOW - nowDOW;
    if (diff < 0) {
      diff = 7 + diff;
    }
    return LocalDate.now().plusDays(diff);
  }


  public class GetPlace extends Thread {
    public void run() {
      log = LogManager.getLogger(GooglePlaces.class);
      Setting setting = settingsService.getSettings();
      List<County> counties = countyService.findAll();

      for (County county : counties
      ) {
        String searchString = setting.getClient() + " Stores in county " + county.getName();
        StringBuilder results = googleService.getPlace(setting.getApiKey(), searchString);
        String status = GoogleService.getStatus(results);
        if (!status.equals("OK")) {
          log.error("Failed to get data with code " + status + " check API key");
          break;
        }
        ArrayList<Place> places = GoogleService.setPlace(results);
        String nextToken = GoogleService.getNextToken(results);
        while (nextToken != null) {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            log.error(e.getMessage());
          }
          StringBuilder nextResults = GoogleService.getNextPlace(setting.getApiKey(), nextToken);
          places.addAll(GoogleService.setPlace(nextResults));
          nextToken = GoogleService.getNextToken(nextResults);
        }
        for (Place place : places
        ) {
          if (place.getName().contains(setting.getClient())) {
            placeService.save(place);
          }
        }
      }
    }
  }
}
