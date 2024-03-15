package com.optimised.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class ExceptionTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Integer storeNo;
  private String storeName;
  private LocalDate changeDate;
  private LocalTime open;
  private LocalTime close;
  @EqualsAndHashCode.Exclude
  private Boolean changed;
  private String systemType;
}
