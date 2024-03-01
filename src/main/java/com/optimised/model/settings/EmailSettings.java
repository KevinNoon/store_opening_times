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
  String mailHost;
  Integer mailPort;
  String mailUser;
  String mailPass;
  Boolean mailAuth;
  Boolean mailStartTLS;
}
