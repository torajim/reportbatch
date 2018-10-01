package com.skplanet.dpa.reportbatch.domain.dmfc.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.dmfc.service.DmfcReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DmfcTask {

    @Autowired
    private DmfcReportJob dmfc;

    @Autowired
    private Settings settings;

    @Scheduled(cron = "${dmfc}")
    public void queryAndPrint(){
        log.info("DMFC setVariables");
        dmfc.setVariables();

        log.info("DMFC Slack Report");
        dmfc.reportToSlack();

        if(settings.SPLUNK_SEND_ENABLE) {
            log.info("DMFC Splunk Report");
            dmfc.reportToSplunk();
        }
    }
}