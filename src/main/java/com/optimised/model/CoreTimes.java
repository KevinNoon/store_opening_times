package com.optimised.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Data
public class CoreTimes {
  public CoreTimes() {
  }
  public CoreTimes(Integer storeNo, String storeName, LocalTime sunOpen, LocalTime sunClose, LocalTime monOpen, LocalTime monClose, LocalTime tueOpen, LocalTime tueClose, LocalTime wedOpen, LocalTime wedClose, LocalTime thuOpen, LocalTime thuClose, LocalTime friOpen, LocalTime friClose, LocalTime satOpen, LocalTime satClose) {
    this.storeNo = storeNo;
    this.storeName = storeName;
    this.sunOpen = sunOpen;
    this.sunClose = sunClose;
    this.monOpen = monOpen;
    this.monClose = monClose;
    this.tueOpen = tueOpen;
    this.tueClose = tueClose;
    this.wedOpen = wedOpen;
    this.wedClose = wedClose;
    this.thuOpen = thuOpen;
    this.thuClose = thuClose;
    this.friOpen = friOpen;
    this.friClose = friClose;
    this.satOpen = satOpen;
    this.satClose = satClose;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  Integer storeNo;
  String storeName;
  LocalTime sunOpen;
  LocalTime sunClose;
  LocalTime monOpen;
  LocalTime monClose;
  LocalTime tueOpen;
  LocalTime tueClose;
  LocalTime wedOpen;
  LocalTime wedClose;
  LocalTime thuOpen;
  LocalTime thuClose;
  LocalTime friOpen;
  LocalTime friClose;
  LocalTime satOpen;
  LocalTime satClose;
}
