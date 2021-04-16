package com.ipo.alarm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmSchedule {

    private final IPOCompanyService ipoCompanyService;

    @Scheduled(cron = "0 30 8 * * *")
    public void alarmCronTask(){
        log.info("alarmCronTask start");
        ipoCompanyService.ipoAlarmService();
    }
}
