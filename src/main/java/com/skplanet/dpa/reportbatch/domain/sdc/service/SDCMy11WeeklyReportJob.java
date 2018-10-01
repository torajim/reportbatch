package com.skplanet.dpa.reportbatch.domain.sdc.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.*;
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

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;

@Component
@BIDB
@Slf4j
public class SDCMy11WeeklyReportJob {
    @Autowired
    private Settings settings;

    @Autowired
    private SDCMy11Mapper sdcMy11Mapper;

    private My11WeeklyAccuracy my11WeeklyAccuracy;
    private My11WeeklyProductCoverage my11WeeklyProductCoverage;

    private void initVariables(){
        my11WeeklyAccuracy = null;
        my11WeeklyProductCoverage = null;
    }

    public void setVariables(int addWeekVal){
        initVariables();
        my11WeeklyAccuracy = sdcMy11Mapper.selectMy11WeeklyAccuracy(addWeekVal);
        my11WeeklyProductCoverage = sdcMy11Mapper.selectMy11WeeklyProductCoverage(addWeekVal);
        log.debug(my11WeeklyAccuracy + "");
        log.debug(my11WeeklyProductCoverage + "");
    }

    public void reportToSplunk(){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "my11week");

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todate = my11WeeklyAccuracy.getTodate();
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

        eventMap.put("fromdate", my11WeeklyAccuracy.getFromdate());
        eventMap.put("todate", my11WeeklyAccuracy.getTodate());
        eventMap.put("total", my11WeeklyAccuracy.getTotal());
        eventMap.put("predictable", my11WeeklyAccuracy.getPredictable());
        eventMap.put("early", my11WeeklyAccuracy.getEarly());
        eventMap.put("exact", my11WeeklyAccuracy.getExact());
        eventMap.put("late", my11WeeklyAccuracy.getLate());
        eventMap.put("coverage", my11WeeklyAccuracy.getCoverage());
        eventMap.put("precision", my11WeeklyAccuracy.getPrecision());
        eventMap.put("prdtotal", my11WeeklyProductCoverage.getPrdTotal());
        eventMap.put("prdpred", my11WeeklyProductCoverage.getPrdPred());
        eventMap.put("prdcoverage", my11WeeklyProductCoverage.getCoverage());
        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}