package com.skplanet.dpa.reportbatch.domain.dmfc.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.DmfcMapper;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.OverallWape;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.WapeByPredModel;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.WapeByProduct;
import com.skplanet.dpa.reportbatch.common.util.SlackTalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DmfcReportJob{

    private Map<WapeCategory, OverallWape> owMap = new HashMap<>();
    private Map<PredAlgorithm, WapeByPredModel> wmMap = new HashMap<>();
    private Set<WapeByProduct> head_product_result_set = new TreeSet<>();
    private Set<WapeByProduct> tail_product_result_set = new TreeSet<>();

    @Autowired
    private DmfcMapper mapper;

    @Autowired
    private Settings settings;

    private void initVariables(){
        owMap = new HashMap<>();
        wmMap = new HashMap<>();
        head_product_result_set = new TreeSet<>();
        tail_product_result_set = new TreeSet<>();
    }

    public void setVariables(){
        initVariables();
        List<OverallWape> owList = mapper.selectOverallWape();
        owList.forEach((s) -> owMap.put(WapeCategory.codeOf(s.getResultType()), s));

        List<WapeByPredModel> wmList = mapper.selectPredModelWape();
        wmList.forEach((s) -> wmMap.put(PredAlgorithm.codeOf(s.getPredAlgClfCd()), s));

        List<WapeByProduct> wpList = mapper.selectProductWape();
        Collections.sort(wpList);
        wpList.forEach((s)->{
            if(head_product_result_set.size() < 5){
                head_product_result_set.add(s);
            }else{
                tail_product_result_set.add(s);
            }
        });
    }

    public void reportToSlack(){
        StringBuffer msg = new StringBuffer();
        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        msg.append("*** " + sdformat.format(date) + " 의 수요 예측 지표 BoT (어제까지 최근 7일분) ***\n");
        cal.add(Calendar.DATE, -7);
        msg.append("대상 기간: " + sdformat.format(cal.getTime()));
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        msg.append(" ~ " + sdformat.format(cal.getTime()) + "\n");

        OverallWape ow_all = owMap.get(WapeCategory.all);
        msg.append("예측 대상 상품 " + ow_all.getStckCnt() + " 종의 판매 수량을 " + ow_all.getPredOrdQty() + "개로 예측. 실제 " + ow_all.getRealOrdQty() + "개 팔림.\n");
        msg.append("오차율(WAPE): " + formatFloat(ow_all.getWape()) + "\n");

        msg.append("\n*** 판매-예측 상황별 예측 성능 ***\n");
        OverallWape ow_lt = owMap.get(WapeCategory.less_than_pred);
        msg.append("1. 예측보다 적게 팔린 경우\n");
        msg.append("예측 대상 상품 중 " + ow_lt.getStckCnt() + "종의 판매 수량을 " + ow_lt.getPredOrdQty() + "개로 예측. 실제 " + ow_lt.getRealOrdQty() + "개 팔림.\n");
        msg.append("오차율(WAPE): " + formatFloat(ow_lt.getWape()) + "\n");
        OverallWape ow_ge = owMap.get(WapeCategory.greater_than_or_equal_to_pred);
        msg.append("2. 예측보다 많이 팔린 경우\n");
        msg.append("예측 대상 상품 중 " + ow_ge.getStckCnt() + "종의 판매 수량을 " + ow_ge.getPredOrdQty() + "개로 예측. 실제 " + ow_ge.getRealOrdQty() + "개 팔림.\n");
        msg.append("오차율(WAPE): " + formatFloat(ow_ge.getWape()) + "\n");

        WapeByPredModel w_ma = wmMap.get(PredAlgorithm.moving_average);
        msg.append("\n*** 알고리즘별 예측 성능 ***\n");
        msg.append("1. MA 예측이 우수한 경우 \n");
        msg.append("예측 대상 상품 중 " + w_ma.getStckCnt() + "종의 판매 수량을 " + w_ma.getPredOrdQty() + "개로 예측. 실제 " + w_ma.getRealOrdQty() + "개 팔림.\n");
        msg.append("오차율(WAPE): " + formatFloat(w_ma.getWape()) + "\n");

        WapeByPredModel w_arima = wmMap.get(PredAlgorithm.arima);
        msg.append("2. ARIMA 예측이 우수한 경우 \n");
        msg.append("예측 대상 상품 중 " + w_arima.getStckCnt() + "종의 판매 수량을 " + w_arima.getPredOrdQty() + "개로 예측. 실제 " + w_arima.getRealOrdQty() + "개 팔림.\n");
        msg.append("오차율(WAPE): " + formatFloat(w_arima.getWape()) + "\n");

        msg.append("\n*** 상품별 예측 성능 (최소 100개 이상 팔린 상품) *** \n");
        msg.append("1. 예측 정확도 상위 5개 상품\n");
        for(WapeByProduct tvo : this.head_product_result_set){
            msg.append("[" + tvo.getAsc_rank() + "] " + tvo.getPrd_nm() + ", 예측:" + formatFloat(tvo.getPredtn_qty()) + ", 실제:" + tvo.getOrd_qty()
                    + " (재고번호:" + tvo.getStck_no() + ", 상품번호:" + tvo.getPrd_no() + ")\n");
        }
        msg.append("2. 예측 정확도 하위 5개 상품\n");
        for(WapeByProduct tvo : this.tail_product_result_set){
            msg.append("[" + tvo.getAsc_rank() + "] " + tvo.getPrd_nm() + ", 예측:" + formatFloat(tvo.getPredtn_qty()) + ", 실제:" + tvo.getOrd_qty()
                    + " (재고번호:" + tvo.getStck_no() + ", 상품번호:" + tvo.getPrd_no() + ")\n");
        }

        SlackTalker.talk(settings.SLACK_ENDPOINT_DMFC,"```" + msg.toString() + "```");
    }

    public void reportToSplunk(){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "dmfc");
        paramMap.put("time", new Date().getTime());
        try {
            paramMap.put("host", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        OverallWape ow_all = owMap.get(WapeCategory.all);
        WapeByPredModel w_ma = wmMap.get(PredAlgorithm.moving_average);
        WapeByPredModel w_arima = wmMap.get(PredAlgorithm.arima);

        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("Prediction_Result(Stock_Count)", ow_all.getStckCnt());
        eventMap.put("Prediction_Result(Sales_Count)", ow_all.getPredOrdQty());
        eventMap.put("Actual_Result(Stock_Count)", ow_all.getStckCnt());
        eventMap.put("Actual_Result(Sales_Count)", ow_all.getRealOrdQty());
        eventMap.put("WAPE(Total)", formatFloat(ow_all.getWape()));
        eventMap.put("WAPE(MA)", formatFloat(w_ma.getWape()));
        eventMap.put("WAPE(ARIMA)", formatFloat(w_arima.getWape()));
        eventMap.put("Accuracy_Top_5", this.head_product_result_set);
        eventMap.put("Accuracy_Bottom_5", this.tail_product_result_set);
        paramMap.put("event", eventMap);

        try {
            //sender.sendKPIs(paramMap, "92814c7d-1ac0-48ec-bda4-fcda95a984f1");
            sender.sendKPIs(paramMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Float formatFloat(double a){
        return Float.parseFloat(String.format("%.2f", a));
    }
}