package com.optimised.controllers.email;

import com.optimised.model.Info;
import com.optimised.model.Logs;
import com.optimised.model.User;
import com.optimised.services.EmailService;
import com.optimised.services.InfoService;
import com.optimised.services.LogService;
import com.optimised.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ErrorController {
  private static Logger log = LogManager.getLogger(ErrorController.class);
  @Autowired
  InfoService infoService;
  @Autowired
  LogService logService;
  @Autowired
  EmailService emailService;
  @Autowired
  UserService userService;

  @Scheduled(fixedRate = 300000)
  private void sendEmail() {
    //Get any records that have not been sent
    //Get the last record number to be emailed
    Optional<Info> info = infoService.findFirst(1l);
    Long lastRecordNo = 0l;
    if (info.isPresent()) {
      lastRecordNo = info.get().getLastLogEmailedRecordNo();
    }
    //Get Error logs with log record > lastRecordNo
    List<Logs> logs = logService.findErrorLogs(lastRecordNo);
    //If found build message for users
    StringBuilder message = new StringBuilder();
    Long lastRecordNoUpdated = lastRecordNo;
    if (logs.size() > 0) {
      for (Logs l : logs
      ) {
        if (l.getId() > lastRecordNoUpdated) lastRecordNoUpdated = l.getId();
        message.append(l.getMessage() + " " + l.getLogDate() + " " + l.getLevel() + "\n");
      }
      message.append("Please investigate");

      //Get users that are set up to get ERROR emails
      List<User> users = userService.findAllErrorEmails();
      if (users.size() > 0) {
        for (User u : users
        ) {
          String result = emailService.sendSimpleMessage(u.getEmail(), "StoreTimesError", message.toString());
          if (result.equals("OK")) {
            info.get().setLastLogEmailedRecordNo(lastRecordNoUpdated);
            infoService.save(info.get());
          } else {
            log.warn("Failed to send eMail " + result);
          }
        }
      }
    }
  }
}
