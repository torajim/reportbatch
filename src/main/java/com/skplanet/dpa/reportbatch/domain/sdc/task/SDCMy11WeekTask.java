package com.skplanet.dpa.reportbatch.domain.sdc.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sdc.service.SDCMy11WeeklyReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SDCMy11WeekTask {
    @Autowired
    private Settings settings;

    @Autowired
    private SDCMy11WeeklyReportJob sdcmy11week;

    @Scheduled(cron = "${sdcmy11week}")
    public void queryAndPrint(){
        log.info("SDCMy11Week basic setVariables");
        sdcmy11week.setVariables(-1);

        if(settings.SPLUNK_SEND_ENABLE) {
            log.info("SDCMy11Week Splunk Report");
            sdcmy11week.reportToSplunk();
        }
    }
}