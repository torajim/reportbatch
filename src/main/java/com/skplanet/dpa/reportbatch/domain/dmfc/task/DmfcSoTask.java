package com.skplanet.dpa.reportbatch.domain.dmfc.task;

import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.dmfc.service.DmfcSOReportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DmfcSoTask {
    @Autowired
    private DmfcSOReportJob dmfcSo;

    @Autowired
    private Settings settings;

    @Scheduled(cron = "${dmfcso}")
    public void queryAndPrint(){
        log.info("DMFCSO setVariables");
        dmfcSo.setVariables();

        if(settings.SPLUNK_SEND_ENABLE){
            log.info("DMFCSO Splunk Report");
            dmfcSo.reportToSplunk();
        }
    }
}