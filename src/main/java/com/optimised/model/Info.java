package com.optimised.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Info {
  @Id
 // @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String lastUpdateTime;
  private Long lastLogEmailedRecordNo;
}
