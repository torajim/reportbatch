package com.skplanet.dpa.reportbatch.domain.hotclkad.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.hotclkad.service.HotClkAdReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotClkAdTask {

    @Autowired
    private HotClkAdReportJob hotclkad;

    @Autowired
    private Settings settings;

    @Scheduled(cron = "${hotclkad}")
    public void queryAndPrint(){
        log.info("HOTCLKAD setVariables");
        hotclkad.setVariables(-1);

        if(settings.SPLUNK_SEND_ENABLE) {
            log.info("HOTCLKAD Splunk Report");
            hotclkad.reportToSplunk(-1);
        }
    }
}