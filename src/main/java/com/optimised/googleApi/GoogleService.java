package com.optimised.googleApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimised.model.Place;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class GoogleService {
  private static Logger log;

  public StringBuilder getPlace(String apiKey, String searchString) {
    log = LogManager.getLogger(GoogleService.class);

    StringBuilder response = new StringBuilder();
    URIBuilder url = null;
    try {
      url = new URIBuilder( Constants.GOOGLE_API_PLACE );
      url.addParameter( "key", apiKey );
      url.addParameter("query",searchString);
    } catch (URISyntaxException e) {
         log.error(e.getMessage());
    }
    googleRequest(response, url.toString());
    return response;
  }

  public static StringBuilder getNextPlace(String apiKey, String pageToken) {
    log = LogManager.getLogger(GoogleService.class);


    StringBuilder response = new StringBuilder();
    URIBuilder url = null;
    try {
      url = new URIBuilder( Constants.GOOGLE_API_PLACE );
      url.addParameter( "key", apiKey );
      url.addParameter("pagetoken",pageToken);
    } catch (URISyntaxException e) {
      log.error(e.getMessage());
    }
    googleRequest(response, url.toString());
    return response;
  }

  public static ArrayList<Place> setPlace(StringBuilder response){
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    ArrayList<Place> places = new ArrayList<>();
    JsonNode rootArray = null;
    try {
      rootArray = objectMapper.readTree(response.toString());
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
    for (JsonNode root: rootArray) {
      if (root.isArray() && !root.isEmpty()) {
        for (JsonNode subRoot : root) {
          if (!subRoot.isMissingNode()) {
            Place place = new Place();
            place.setPlaceId(subRoot.get("place_id").asText());
            place.setName(subRoot.get("name").asText());
            place.setAddress(subRoot.get("formatted_address").asText());
            places.add(place);
          }
        }
      }
    }
    return places;
  }

  public static StringBuilder getPlaceDetails(String apiKey, String apiPlaceId) {
    StringBuilder response = new StringBuilder();
    String apiUrl = Constants.GOOGLE_API_PLACE_DETAILS +
        "?placeid=" + apiPlaceId +
        "&key=" + apiKey;
    googleRequest(response, apiUrl);
    return response;
  }

  public static Place setPlaceDetails(StringBuilder response){
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Place place = new Place();
    JsonNode rootArray;
    try {
      rootArray = objectMapper.readTree(response.toString());

      for (JsonNode root : rootArray) {

        if ( !root.isEmpty()) {
          place.setPlaceId(root.get("place_id").asText());

          if (root.get("name") != null) place.setName(root.get("name").asText());
          if (root.get("website") != null) place.setWebSite(root.get("website").asText());
          if (root.get("url") != null) place.setGoogleUrl(root.get("url").asText());
          if (root.get("formatted_phone_number") != null) place.setPhoneNo(root.get("formatted_phone_number").asText());
          if (root.get("formatted_address") != null) {
            String[] shortAddressBits = root.get("formatted_address").asText().split(",");
            String shortAddress = "";
            if (shortAddressBits.length > 1) {
              shortAddress = shortAddressBits[shortAddressBits.length - 2] + " " + shortAddressBits[shortAddressBits.length - 1];
            }
            place.setAddress(root.get("formatted_address").asText());
            place.setName(place.getName() + " " + shortAddress);
          }

          if (root.get("opening_hours") != null) {
            JsonNode periods = root.get("opening_hours").get("periods");
            if (!periods.isEmpty()) {
              JsonNode openTime = null;
              JsonNode closeTime = null;
//              Map<Integer, LocalTime> openTimes = new HashMap<>();
//              Map<Integer, LocalTime> closeTimes = new HashMap<>();
              for (JsonNode period : periods) {
                openTime = period.get("open");
                closeTime = period.get("close");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
                LocalTime ot = LocalTime.parse(openTime.get("time").asText(), formatter);
                LocalTime ct = LocalTime.parse(closeTime.get("time").asText(), formatter);
                switch(openTime.get("day").asInt()){
                  case 0:
                    place.setSunOpen(ot);
                    place.setSunClose(ct);
                    break;
                  case 1:
                    place.setMonOpen(ot);
                    place.setMonClose(ct);
                    break;
                  case 2:
                    place.setTueOpen(ot);
                    place.setTueClose(ct);
                    break;
                  case 3:
                    place.setWedOpen(ot);
                    place.setWedClose(ct);
                    break;
                  case 4:
                    place.setThuOpen(ot);
                    place.setThuClose(ct);
                    break;
                  case 5:
                    place.setFriOpen(ot);
                    place.setFriClose(ct);
                    break;
                  case 6:
                    place.setSatOpen(ot);
                    place.setSatClose(ct);
                    break;
                }
//                openTimes.put(openTime.get("day").asInt(), LocalTime.parse(openTime.get("time").asText(), formatter));
//                closeTimes.put(closeTime.get("day").asInt(), LocalTime.parse(closeTime.get("time").asText(), formatter));

              }
//              for (int i = 0; i < 7; i++) {
//                LocalTime open = LocalTime.of(0, 0);
//                if (openTimes.get(i) != null) open = openTimes.get(i);
//                LocalTime close = LocalTime.of(0, 0);
//                if (closeTimes.get(i) != null) close = closeTimes.get(i);
//                place.getOpeningHours().add(new OpeningHours(Constants.GOOGLE_API_DAYS.get(i), open, close));
//              }
              JsonNode location = root.get("geometry").get("location");
              place.setLocationLat(location.get("lat").asDouble());
              place.setLocationLng(location.get("lng").asDouble());

              place.setInuse(true);
            }
          }
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return place;
  }

  private static void googleRequest(StringBuilder response, String apiUrl) {
    try {
      URL url = new URL(apiUrl);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      // Set the request method to GET
      connection.setRequestMethod("GET");
      // Get the response from the API
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();
    } catch (ProtocolException e) {
      log.error(e.getMessage());;
    } catch (MalformedURLException e) {
      log.error(e.getMessage());;
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public static String getStatus(StringBuilder response){
    String status = "";
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      JsonNode rootArray = objectMapper.readTree(response.toString());
      status = rootArray.get("status").asText();
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
    return status;
  }

  public static String getNextToken(StringBuilder response){
    String nextToken = null;
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      JsonNode rootArray = objectMapper.readTree(response.toString());
      if(rootArray.get("next_page_token") != null) nextToken = rootArray.get("next_page_token").asText();
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
    return nextToken;
  }
}
