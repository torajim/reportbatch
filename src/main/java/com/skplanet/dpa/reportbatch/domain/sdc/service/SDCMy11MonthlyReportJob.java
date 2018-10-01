package com.skplanet.dpa.reportbatch.domain.sdc.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.My11MonthlyAccuracy;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.My11MonthlyProductCoverage;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.SDCMy11Mapper;
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
public class SDCMy11MonthlyReportJob {
    @Autowired
    private Settings settings;

    @Autowired
    private SDCMy11Mapper sdcMy11Mapper;

    private My11MonthlyAccuracy my11MonthlyAccuracy;
    private My11MonthlyProductCoverage my11MonthlyProductCoverage;

    private void initVariables(){
        my11MonthlyAccuracy = null;
        my11MonthlyProductCoverage = null;
    }

    public void setVariables(int addMonVal){
        initVariables();
        my11MonthlyAccuracy = sdcMy11Mapper.selectMy11MonthlyAccuracy(addMonVal);
        my11MonthlyProductCoverage = sdcMy11Mapper.selectMy11MonthlyProductCoverage(addMonVal);
        log.debug(my11MonthlyAccuracy + "");
        log.debug(my11MonthlyProductCoverage + "");
    }

    public void reportToSplunk(){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "my11mon");

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todate = my11MonthlyAccuracy.getTodate();
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

        eventMap.put("fromdate", my11MonthlyAccuracy.getFromdate());
        eventMap.put("todate", my11MonthlyAccuracy.getTodate());
        eventMap.put("total", my11MonthlyAccuracy.getTotal());
        eventMap.put("predictable", my11MonthlyAccuracy.getPredictable());
        eventMap.put("early", my11MonthlyAccuracy.getEarly());
        eventMap.put("exact", my11MonthlyAccuracy.getExact());
        eventMap.put("late", my11MonthlyAccuracy.getLate());
        eventMap.put("coverage", my11MonthlyAccuracy.getCoverage());
        eventMap.put("precision", my11MonthlyAccuracy.getPrecision());
        eventMap.put("prdtotal", my11MonthlyProductCoverage.getPrdTotal());
        eventMap.put("prdpred", my11MonthlyProductCoverage.getPrdPred());
        eventMap.put("prdcoverage", my11MonthlyProductCoverage.getCoverage());
        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}