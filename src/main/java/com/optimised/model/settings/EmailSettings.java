package com.optimised.model.settings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table
public class EmailSettings {
  @Id
  private Long id;
  private String mailHost;
  private Integer mailPort;
  private String mailUser;
  private String mailPass;
  private Boolean mailAuth;
  private Boolean mailStartTLS;
}
