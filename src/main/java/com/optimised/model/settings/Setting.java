package com.optimised.model.settings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalTime;

@Entity
@Data
@Table
public class Setting {
    @Id
    private Long id;
    private String client;
    private Boolean enableAutoUpdate;
    private LocalTime updateTime;
    private String csvChgTempDir;
    @NotEmpty
    @Length(min = 8,max = 16)
    private String csvChgSuffix;
    private String csvChgName;
    private String changeFlagReset;
    private String apiKey;
}
