package com.optimised.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "store_system")
@Data
public class StoreSystem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Length(min = 2, max = 20, message = "Client must have a minimum of 2 charters")
  private String name;
}

