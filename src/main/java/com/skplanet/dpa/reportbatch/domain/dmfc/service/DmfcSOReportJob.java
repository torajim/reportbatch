package com.skplanet.dpa.reportbatch.domain.dmfc.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.*;
import com.skplanet.dpa.reportbatch.common.util.SlackTalker;
import com.skplanet.dpa.reportbatch.common.util.XlsGenUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DmfcSOReportJob{

    private List<String> headers_overview1 = new ArrayList<String>(Arrays.asList(new String[]{"담당MD", "판매중", "품절", "품절율"}));
    private List<String> headers_now = new ArrayList<String>(Arrays.asList(new String[]{"상품번호", "재고번호", "상품명", "담당MD", "가용재고", "기발주", "적치대기", "지난1주간판매수량", "지난2주간판매수량", "지난4주간판매수량", "판매상태", "전시여부", "재고속성", "배송유형", "품절기간", "센터명"}));
    private List<String> headers_pred = new ArrayList<String>(Arrays.asList(new String[]{"상품번호", "재고번호", "상품명", "담당MD", "가용재고", "기발주", "적치대기", "전체재고", "D0__D6예측판매량", "지난1주간판매수량", "지난2주간판매수량", "지난4주간판매수량", "판매상태", "재고속성", "배송유형"}));
    private List<String> headers_pic = new ArrayList<String>(Arrays.asList(new String[]{"담당MD", "상품번호", "상품명"}));

    private List<MdSoRate> body_overview1 = new ArrayList<>();
    private List<SoStatus> body_now_11 = new ArrayList<>();
    private List<SoStatus> body_now_seller = new ArrayList<>();
    private List<SoPrediction> body_pred = new ArrayList<>();
    private List<MdProduct> body_pic = new ArrayList<>();

    private long sumSellCnt = 0;
    private long sumSoCnt = 0;
    private float sumSoRate = 0.0f;

    @Autowired
    private DmfcSoMapper mapper;

    @Autowired
    private Settings settings;

    private void initVariables(){
        body_overview1 = new ArrayList<>();
        body_now_11 = new ArrayList<>();
        body_now_seller = new ArrayList<>();
        body_pred = new ArrayList<>();
        body_pic = new ArrayList<>();

        sumSellCnt = 0;
        sumSoCnt = 0;
        sumSoRate = 0.0f;
    }

    public void setVariables(){
        initVariables();
        this.body_overview1 = mapper.selectMdSoRates();
        this.body_now_11 = mapper.selectSoStatusDeliveredBy11st();
        this.body_now_seller = mapper.selectSoStatusDeliveredBySeller();
        this.body_pred = mapper.selectSoPredictions();
        this.body_pic = mapper.selectMdProducts();

        for(MdSoRate mdSoRate : body_overview1){
            sumSellCnt += mdSoRate.getSellCnt();
            sumSoCnt += mdSoRate.getSoCnt();
        }
        MdSoRate sumMdSoRate = new MdSoRate();
        sumMdSoRate.setMdName("합계");
        sumMdSoRate.setSellCnt(sumSellCnt);
        sumMdSoRate.setSoCnt(sumSoCnt);
        sumSoRate = (float)sumSoCnt / (float)(sumSellCnt + sumSoCnt) * 100.0f;
        sumMdSoRate.setSoRate(formatFloat(sumSoRate));
        body_overview1.add(sumMdSoRate);
    }

    public void reportToSlack(){
        StringBuffer msg = new StringBuffer();
        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        msg.append("**" + sdformat.format(date) + " 의 품절 예측 리포트**\n");
        msg.append("**** 담당 MD 별 품절 상품 목록 ****\n");

        headers_overview1.forEach((s) -> msg.append(s + "   "));
        msg.append("\n");
        body_overview1.forEach((s) -> msg.append(fillB(s.getMdName(), 4)
                + "   " + fillB(s.getSellCnt()+"", 4)
                + "   " + fillB(s.getSoCnt()+"", 4)
                + "   " + fillB(s.getSoRate()+"", 4)
                + "\n"
        ));

        XlsGenUtil xlsgen = new XlsGenUtil();
        Workbook sheet_over = xlsgen.createExcel(headers_overview1, Arrays.asList(body_overview1.toArray()), null, "담당MD별_품절현황");
        Workbook sheet_now_11 = xlsgen.createExcel(headers_now, Arrays.asList(body_now_11.toArray()), sheet_over, "품절현황_11번가배송");
        sheet_over = null;
        Workbook sheet_now_seller = xlsgen.createExcel(headers_now, Arrays.asList(body_now_seller.toArray()), sheet_now_11, "품절현황_업체배송");
        sheet_now_11 = null;
        Workbook sheet_pred = xlsgen.createExcel(headers_pred, Arrays.asList(body_pred.toArray()), sheet_now_seller, "품절예측");
        sheet_now_seller = null;
        Workbook sheet_pic = xlsgen.createExcel(headers_pic, Arrays.asList(body_pic.toArray()), sheet_pred, "담당MD별_관리상품");
        sheet_pred = null;

        String fileName = sdformat.format(date) + "_sold_out";
        try {
            xlsgen.makeFileWithWorkbook(fileName, sheet_pic);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SlackTalker.talk(settings.SLACK_ENDPOINT_DMFCSO, "```" + msg.toString() + "```");
        SlackTalker.uploadFile(settings.SLACK_BOT_TOKEN, new File(fileName + ".xls"), settings.SLACK_BOT_DMFCSO, fileName, sdformat.format(date) + "의 품절 현황입니다!");
    }

    public void reportToSplunk(){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "dmfcso");

        paramMap.put("time", new Date().getTime());
        try {
            paramMap.put("host", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("sellcnt", this.sumSellCnt);
        eventMap.put("socnt", this.sumSoCnt);
        eventMap.put("sorate", this.sumSoRate);
        eventMap.put("mdsolist", this.body_overview1);
        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Float formatFloat(float a){
        return Float.parseFloat(String.format("%.1f", a));
    }

    private static String fillB(String a, int limit){
        if(a == null || a.length() == 0 || a.length() == limit){
            return a;
        }

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < limit - a.length(); i++){
            sb.append(" ");
        }
        return sb.append(a).toString();
    }
}