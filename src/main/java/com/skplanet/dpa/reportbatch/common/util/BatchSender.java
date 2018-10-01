package com.skplanet.dpa.reportbatch.common.util;

import com.skplanet.dpa.reportbatch.domain.dex.service.DexReportJob;
import com.skplanet.dpa.reportbatch.domain.hotclkad.service.HotClkAdReportJob;
import com.skplanet.dpa.reportbatch.domain.sdc.service.SDCMy11MonthlyReportJob;
import com.skplanet.dpa.reportbatch.domain.sdc.service.SDCMy11WeeklyReportJob;
import com.skplanet.dpa.reportbatch.domain.sellerpcs.service.SellerPcsReportJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchSender {
    @Autowired
    private HotClkAdReportJob hotClkAdReportJob;

    @Autowired
    private SellerPcsReportJob sellerPcsReportJob;

    @Autowired
    private SDCMy11MonthlyReportJob sdcMy11MonthlyReportJob;

    @Autowired
    private SDCMy11WeeklyReportJob sdcMy11WeeklyReportJob;

    @Autowired
    private DexReportJob dexReportJob;

    public void sendHotClkAd(int addDayVal){
        for (; addDayVal <= -1; addDayVal++) {
            hotClkAdReportJob.setVariables(addDayVal);
            hotClkAdReportJob.reportToSplunk(addDayVal);
        }
    }

    public void sendSellerPcs(int addDayVal){
        for(; addDayVal <= -1; addDayVal++){
            sellerPcsReportJob.setVariables(addDayVal);
            sellerPcsReportJob.reportToSplunk(addDayVal);
        }
    }

    public void sendSDCMy11Mon(int addMonVal){
        for(; addMonVal <= -1; addMonVal++){
            sdcMy11MonthlyReportJob.setVariables(addMonVal);
            sdcMy11MonthlyReportJob.reportToSplunk();
        }
    }

    public void sendSDCMy11Week(int addWeekVal){
        for(; addWeekVal <= -1; addWeekVal++){
            sdcMy11WeeklyReportJob.setVariables(addWeekVal);
            sdcMy11WeeklyReportJob.reportToSplunk();
        }
    }

    public void sendDexQaLog(int addDayVal){
        for (; addDayVal <= -1; addDayVal++) {
            dexReportJob.setVariables(addDayVal);
            dexReportJob.reportToSplunk();
        }
    }
}