package com.optimised.repository.settings;

import com.optimised.model.settings.EmailSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSettingsRepo extends JpaRepository<EmailSettings,Long> {
  EmailSettings findFirstBy();
}
