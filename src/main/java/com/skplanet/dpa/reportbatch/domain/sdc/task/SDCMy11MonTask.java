package com.skplanet.dpa.reportbatch.domain.sdc.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sdc.service.SDCMy11MonthlyReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SDCMy11MonTask {
    @Autowired
    private Settings settings;

    @Autowired
    private SDCMy11MonthlyReportJob sdcmy11mon;

    @Scheduled(cron = "${sdcmy11mon}")
    public void queryAndPrint(){
        log.info("SDCMy11Mon basic setVariables");
        sdcmy11mon.setVariables(-1);

        if(settings.SPLUNK_SEND_ENABLE) {
            log.info("SDCMy11Mon Splunk Report");
            sdcmy11mon.reportToSplunk();
        }
    }
}