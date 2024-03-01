package com.optimised.controllers.email;

import com.optimised.model.ExceptionTime;
import com.optimised.model.User;
import com.optimised.services.EmailService;
import com.optimised.services.ExceptionTimeService;
import com.optimised.services.UserService;
import com.optimised.services.settings.SettingsService;
import com.optimised.tools.Excel;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CsvController {
  private static Logger log = LogManager.getLogger(CsvController.class);
  @Autowired
  SettingsService settingsService;
  @Autowired
  ExceptionTimeService exceptionTimeService;
  @Autowired
  UserService userService;
  @Autowired
  EmailService emailService;

//  @Scheduled(cron = "0 01 * * ?") //Run at 01:00am every day
    @Scheduled(fixedRate = 30000)
  private void sendEmail(){
    if (settingsService.getSettings().getChangeFlagReset().equals("CSV")){
      //Get all the new exceptions
      List<ExceptionTime> exceptionTimes = exceptionTimeService.findByChanged();
      if (exceptionTimes.size() > 0){
        String fileName = Excel.createCsv(exceptionTimes);
        List<User> users = userService.findAllCsvEmails();
        if (users.size() > 0){
          for (User u:users
               ) {
            String result = null;
            try {
              result = emailService.sendMessageWithAttachment(u.getEmail(),"CSV Exceptions","CSV Exceptions",fileName);
              if (result.equals("OK")){
                log.info("CSV exception Mail sent to " + u.getEmail());
                //Reset the change flag
                for (ExceptionTime et:exceptionTimes
                     ) {
                  exceptionTimeService.setChangedFalse(et);
                }
              } else {
                log.warn("Failed to send CSV exception to " + u.getEmail());
              }
            } catch (IOException e) {
              log.error(e.getMessage());
            } catch (MessagingException e) {
              log.error(e.getMessage());
            }
          }
        }
      }
    }
  }
}
