package com.optimised.services.settings;

import com.optimised.model.settings.EmailSettings;
import com.optimised.repository.settings.EmailSettingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailSettingsService {
  @Autowired
  EmailSettingsRepo emailSettingsRepo;

  public EmailSettingsService(EmailSettingsRepo emailSettingsRepo) {
    this.emailSettingsRepo = emailSettingsRepo;
  }

  public EmailSettings getSettings(){
    if (emailSettingsRepo.findFirstBy() == null){
      EmailSettings emailSettings = new EmailSettings();
      emailSettings.setId(1l);
      emailSettings.setMailHost("smtp.gmail.com");
      emailSettings.setMailPort(587);
      emailSettings.setMailUser("");
      emailSettings.setMailPass("");
      emailSettings.setMailAuth(true);
      emailSettings.setMailStartTLS(true);
      emailSettingsRepo.save(emailSettings);
    }
    return emailSettingsRepo.findFirstBy();
  }

  public void save(EmailSettings emailSettings){
    emailSettingsRepo.save(emailSettings);
  }
}
