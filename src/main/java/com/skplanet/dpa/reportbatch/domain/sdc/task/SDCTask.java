package com.skplanet.dpa.reportbatch.domain.sdc.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sdc.service.SDCReportJob;
import com.skplanet.dpa.reportbatch.domain.sellerpcs.service.SellerPcsReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SDCTask {

    @Autowired
    private SDCReportJob sdc;

    @Autowired
    private Settings settings;

    @Scheduled(cron = "${sdc}")
    public void queryAndPrint(){
        log.info("SDC basic setVariables");
        sdc.setVariables();

        log.info("SDC pred setVariables");
        sdc.setPredictionVariables();

        log.info("SDC Slack Report");
        sdc.reportToSlack();

        if(settings.SPLUNK_SEND_ENABLE) {
            log.info("SDC Splunk Report");
            sdc.reportToSplunk();
        }
    }
}