package com.optimised.repository.settings;


import com.optimised.model.settings.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepo extends JpaRepository<Setting,Long> {
    Setting findFirstBy();
}
