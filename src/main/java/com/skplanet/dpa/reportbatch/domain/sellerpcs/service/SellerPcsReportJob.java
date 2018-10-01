package com.skplanet.dpa.reportbatch.domain.sellerpcs.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sellerpcs.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SellerPcsReportJob {
    private DailyStat dailyStat;
    private MonthlyStat monthlyStat;
    private List<ChartViewCtlg> chartViewCtlgList;
    private SoMainStat soMainStat;

    @Autowired
    private SellerPcsMapper sellerPcsMapper;

    @Autowired
    private Settings settings;

    private void initVariables(){
        DailyStat dailyStat = null;
        MonthlyStat monthlyStat = null;
        List<ChartViewCtlg> chartViewCtlgList = null;
        soMainStat = null;
    }

    public void setVariables(int addDayVal){
        initVariables();
        dailyStat = sellerPcsMapper.selectDailyStat(addDayVal);
        monthlyStat = sellerPcsMapper.selectMonthlyStat(addDayVal);
        chartViewCtlgList = sellerPcsMapper.selectChartViewCtlgTop10(addDayVal);
        soMainStat = sellerPcsMapper.selectSoMainStat(addDayVal);

        log.debug(dailyStat + "");
        log.debug(monthlyStat + "");
        log.debug(chartViewCtlgList + "");
        log.debug(soMainStat + "");
    }

    public void reportToSplunk(int addDayVal){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "sellerpcs");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, addDayVal);

        paramMap.put("time", cal.getTime().getTime());
        try {
            paramMap.put("host", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("dau", dailyStat.getDau());
        eventMap.put("pv", dailyStat.getPv());
        eventMap.put("avgVf", dailyStat.getAvgVf());
        eventMap.put("prcSortClk", dailyStat.getPrcSortClk());
        eventMap.put("avgSc", dailyStat.getAvgSc());
        eventMap.put("chartView", dailyStat.getChartView());
        eventMap.put("avgCv", dailyStat.getAvgCv());
        eventMap.put("alarmSetupClk", dailyStat.getAlarmSetupClk());
        eventMap.put("acmAlarmSetupClk", dailyStat.getAcmAlarmSetupClk());
        eventMap.put("avgAlarmSetupClk", dailyStat.getAvgAlarmSetupClk());
        eventMap.put("prdAlarmOnClk", dailyStat.getPrdAlarmOnClk());
        eventMap.put("prdAlarmOffClk", dailyStat.getPrdAlarmOffClk());
        eventMap.put("acmPrdAlarmOn", dailyStat.getAcmPrdAlarmOn());
        eventMap.put("avgPrdAlarmClk", dailyStat.getAvgPrdAlarmClk());
        eventMap.put("mngrSelCnt", dailyStat.getMngrSelCnt());
        eventMap.put("acmMngrSelCnt", dailyStat.getAcmMngrSelCnt());
        eventMap.put("mau", monthlyStat.getMau());
        eventMap.put("totSelCnt", monthlyStat.getTotSelCnt());
        eventMap.put("chartClkCatalogTop10", chartViewCtlgList);

        eventMap.put("prdAdjClk", dailyStat.getPrdAdjClk());
        eventMap.put("prdAdjCnfmCnt", dailyStat.getPrdAdjCnfmCnt());
        eventMap.put("prcAdjCnfmSelCnt", dailyStat.getPrcAdjCnfmSelCnt());
        eventMap.put("prdAdjDscAmt", dailyStat.getPrdAdjDscAmt());

        eventMap.put("soMainPv", soMainStat.getSoMainPv());
        eventMap.put("soMainSelCnt", soMainStat.getSoMainSelCnt());

        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}