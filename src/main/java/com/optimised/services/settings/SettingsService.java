package com.optimised.services.settings;


import com.optimised.model.settings.Setting;
import com.optimised.repository.settings.SettingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepo settingsRepo;

    public SettingsService(SettingsRepo settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    public Setting getSettings(){
        if (settingsRepo.findFirstBy() == null){
            Setting setting = new Setting();
            setting.setId(1l);
            setting.setUpdateTime(LocalTime.of(1,0));
            setting.setEnableAutoUpdate(true);
            setting.setCsvChgName("OpenTimesChanged");
            setting.setCsvChgSuffix("yyMMddHHmmss");
            setting.setCsvChgTempDir("C:\\Temp\\");
            settingsRepo.save(setting);
        }
        return settingsRepo.findFirstBy();
    }

    public void save(Setting settings){
        settingsRepo.save(settings);
    }
}
