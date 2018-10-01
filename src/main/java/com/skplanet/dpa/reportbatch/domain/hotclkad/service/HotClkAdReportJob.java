package com.skplanet.dpa.reportbatch.domain.hotclkad.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.domain.hotclkad.repository.CtrRps;
import com.skplanet.dpa.reportbatch.domain.hotclkad.repository.HotClkAdMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HotClkAdReportJob {
    private CtrRps ctrRps;

    @Autowired
    private HotClkAdMapper hotClkAdMapper;

    private void initVariables(){
        ctrRps = null;
    }

    public void setVariables(int addDayVal){
        initVariables();
        ctrRps = hotClkAdMapper.selectCtrRps(addDayVal);
        log.debug(ctrRps.toString());
    }

    public void reportToSplunk(int addDayVal){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "hotclkad");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, addDayVal);

        paramMap.put("time", cal.getTime().getTime());
        try {
            paramMap.put("host", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Map<String, Object> eventMap = new HashMap<String, Object>();

        eventMap.put("old_ctr", ctrRps.getOldCtr());
        eventMap.put("model_v1_ctr", ctrRps.getV1Ctr());
        eventMap.put("model_v2_ctr", ctrRps.getV2Ctr());
        eventMap.put("old_rps", ctrRps.getOldRps());
        eventMap.put("model_v1_rps", ctrRps.getV1Rps());
        eventMap.put("model_v2_rps", ctrRps.getV2Rps());
        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}