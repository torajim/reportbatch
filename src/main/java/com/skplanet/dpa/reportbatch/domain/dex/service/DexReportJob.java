package com.skplanet.dpa.reportbatch.domain.dex.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.dex.repository.DailyQaReport;
import com.skplanet.dpa.reportbatch.domain.dex.repository.DexMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DexReportJob {
    @Autowired
    private Settings settings;

    @Autowired
    private DexMapper mapper;

    private DailyQaReport dailyQaReport = null;

    private void initVariables(){
        dailyQaReport = null;
    }

    public void setVariables(int addDayVal){
        initVariables();
        dailyQaReport = mapper.selectDailyQaReport(addDayVal);
        log.debug(dailyQaReport + "");
    }

    public void reportToSplunk(){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "dex");

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todate = dailyQaReport.getBaseDt();
        try {
            cal.setTime(sdf.parse(todate));
        } catch (ParseException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        paramMap.put("time", cal.getTime().getTime());

        try {
            paramMap.put("host", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Map<String, Object> eventMap = new HashMap<String, Object>();

        eventMap.put("total", dailyQaReport.getTotal());
        eventMap.put("base_dt", dailyQaReport.getBaseDt());
        eventMap.put("impression", dailyQaReport.getImpression());
        eventMap.put("not_impression", dailyQaReport.getNotImpression());
        eventMap.put("impression_healthy", dailyQaReport.getImpressionHealthy());
        eventMap.put("impression_maybe_healthy", dailyQaReport.getImpressionMaybeHealthy());
        eventMap.put("impression_unhealthy", dailyQaReport.getImpressionUnhealthy());
        eventMap.put("not_impression_healthy", dailyQaReport.getNotImpressionHealthy());
        eventMap.put("not_impression_maybe_healthy", dailyQaReport.getNotImpressionMaybeHealthy());
        eventMap.put("not_impression_unhealthy", dailyQaReport.getNotImpressionUnhealthy());
        eventMap.put("unhealthy_rate", dailyQaReport.getUnhealthyRate());
        eventMap.put("impression_unhealthy_rate", dailyQaReport.getImpressionUnhealthyRate());
        eventMap.put("not_impression_unhealthy_rate", dailyQaReport.getNotImpressionUnhealthyRate());
        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}