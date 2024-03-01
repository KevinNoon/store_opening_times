package com.optimised.googleApi;

import java.util.Map;

import static java.util.Map.entry;

public class Constants {

 // public final static String GOOGLE_API_KEY = "AIzaSyCCB3M_ETdfc-sWkcnN_YXDlUoulI4vKuM";
  public final static String GOOGLE_API_PLACE = "https://maps.googleapis.com/maps/api/place/textsearch/json";
  public final static String GOOGLE_API_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json";
  public enum changeFlagResetValue {CSV,API_OPEN,API_CLOSED};
  public final static Map<Integer,String> GOOGLE_API_DAYS =
      Map.ofEntries (entry(0, "Sun"),entry(1, "Mon"),entry(2, "Tue"),entry(3, "Wed"),
          entry(4, "Thr"),entry(5, "Fri"),entry(6, "Sat"));


}
