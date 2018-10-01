package com.skplanet.dpa.reportbatch.domain.sellerpcs.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sellerpcs.service.SellerPcsReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellerPcsTask {

    @Autowired
    private SellerPcsReportJob sellerpcs;

    @Autowired
    private Settings settings;

    @Scheduled(cron = "${sellerpcs}")
    public void queryAndPrint(){
        log.info("SELLERPCS setVariables");
        sellerpcs.setVariables(-1);

        if(settings.SPLUNK_SEND_ENABLE) {
            log.info("SELLERPCS Splunk Report");
            sellerpcs.reportToSplunk(-1);
        }
    }
}