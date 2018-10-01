package com.skplanet.dpa.reportbatch.domain.dex.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.dex.service.DexReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DexTask {
    @Autowired
    private DexReportJob dex;

    @Autowired
    private Settings settings;

    @Scheduled(cron = "${dex}")
    public void queryAndPrint(){
        log.info("DEX setVariables");
        dex.setVariables(-1);

        if(settings.SPLUNK_SEND_ENABLE){
            log.info("DEX Splunk Report");
            dex.reportToSplunk();
        }
    }
}