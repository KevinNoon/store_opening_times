package com.optimised.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "place")
public class Place implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Exclude
  private Long id;
  private String name;
  @Column(name = "store_no")
  private Integer storeNo;
  @Column(name = "store_system")
  private String storeSystem;
  @Column(unique = true)
  private String placeId;
  @EqualsAndHashCode.Exclude
  private String address;
  @Column(name = "phone_no")
  private String phoneNo;
  @Column(name = "web_site")
  private String webSite;
  @Column(name = "google_url")
  private  String googleUrl;
  private Double locationLat;
  private Double locationLng;
  @EqualsAndHashCode.Exclude
  private Boolean inuse;

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

//  @EqualsAndHashCode.Exclude
//  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//  @JoinColumn(name = "place_id",referencedColumnName = "id")
//  private Set<OpeningHours> openingHours = new HashSet<>();
}
