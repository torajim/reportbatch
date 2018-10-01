package com.skplanet.dpa.reportbatch.domain.sdc.service;

import com.skplanet.dpa.SplunkSender;
import com.skplanet.dpa.reportbatch.common.config.Settings;
import com.skplanet.dpa.reportbatch.common.util.SlackTalker;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.CategoryRow;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.PredictionByDate;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.SDCMapper;
import com.skplanet.dpa.reportbatch.domain.sdc.repository.WeeklyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;

/**
 * Created by 1003724 on 2017-11-01.
 */
@Component
@BIDB
@Slf4j
public class SDCReportJob {

    @Autowired
    private Settings settings;

    @Autowired
    private SDCMapper sdcMapper;

    // Basic Variables
    private WeeklyResult weekdayResult;
    private WeeklyResult weekendResult;

    private float delta_ord_to_send_weekday_of_two_weeks;
    private float delta_ord_to_send_weekend_of_two_weeks;
    private float delta_ord_to_dlv_weekday_of_two_weeks;
    private float delta_ord_to_dlv_weekend_of_two_weeks;
    private long delta_over_5days_to_dlv_weekday_of_two_weeks;
    private long delta_over_5days_to_dlv_weekend_of_two_weeks;

    private Set<CategoryRow> head_late_dlv_ctgr = new TreeSet<>();
    private Set<CategoryRow> tail_late_dlv_ctgr = new TreeSet<>();

    // Prediction Variables
    private Float pred_accuracy;
    private Set<PredictionByDate> pred_acc_by_date = new TreeSet<>();

    private long pred_cnt_exact;
    private long pred_cnt_early;
    private long pred_cnt_late;

    private Set<CategoryRow> pred_head_acc_ctgr = new TreeSet<>();
    private Set<CategoryRow> pred_tail_acc_ctgr = new TreeSet<>();
    private Set<CategoryRow> pred_head_acc_region = new TreeSet<>();
    private Set<CategoryRow> pred_tail_acc_region = new TreeSet<>();

    private void initVariables(){
        // Basic Variables
        weekdayResult = null;
        weekendResult = null;

        delta_ord_to_send_weekday_of_two_weeks = 0;
        delta_ord_to_send_weekend_of_two_weeks = 0;
        delta_ord_to_dlv_weekday_of_two_weeks = 0;
        delta_ord_to_dlv_weekend_of_two_weeks = 0;
        delta_over_5days_to_dlv_weekday_of_two_weeks = 0;
        delta_over_5days_to_dlv_weekend_of_two_weeks = 0;

        head_late_dlv_ctgr = new TreeSet<>();
        tail_late_dlv_ctgr = new TreeSet<>();

        // Prediction Variables
        pred_accuracy = 0.0f;
        pred_acc_by_date = new TreeSet<>();

        pred_cnt_exact = 0;
        pred_cnt_early = 0;
        pred_cnt_late = 0;

        pred_head_acc_ctgr = new TreeSet<>();
        pred_tail_acc_ctgr = new TreeSet<>();
        pred_head_acc_region = new TreeSet<>();
        pred_tail_acc_region = new TreeSet<>();
    }

    public void setVariables(){
        initVariables();
        Connection con = null;

        List<WeeklyResult> weeklyResults = sdcMapper.selectWeeklyResults();
        for(WeeklyResult weeklyResult : weeklyResults){
            int resultType = -1;
            resultType = weeklyResult.getOrd_type();
            if(resultType == 0){
                weekdayResult = weeklyResult;
            }else if(resultType == 1){
                weekendResult = weeklyResult;
            }else{
                log.error("WeeklyResult's resultType is not valid:" + resultType);
            }
        }

        List<WeeklyResult> oldWeeklyResults = sdcMapper.selectOldWeeklyResults();
        for(WeeklyResult weeklyResult : oldWeeklyResults){
            int resultType = -1;
            resultType = weeklyResult.getOrd_type();
            if(resultType == 0){
                this.delta_ord_to_send_weekday_of_two_weeks = weekdayResult.getAvg_ord2snd_hr() - weeklyResult.getAvg_ord2snd_hr();
                this.delta_ord_to_dlv_weekday_of_two_weeks = weekdayResult.getAvg_ord2dlv_hr() - weeklyResult.getAvg_ord2dlv_hr();
                this.delta_over_5days_to_dlv_weekday_of_two_weeks = weekdayResult.getCnt_ord2dlv_late() - weeklyResult.getCnt_ord2dlv_late();
            }else if(resultType == 1){
                this.delta_ord_to_send_weekend_of_two_weeks = weekendResult.getAvg_ord2snd_hr() - weeklyResult.getAvg_ord2snd_hr();
                this.delta_ord_to_dlv_weekend_of_two_weeks = weekendResult.getAvg_ord2dlv_hr() - weeklyResult.getAvg_ord2dlv_hr();
                this.delta_over_5days_to_dlv_weekend_of_two_weeks = weekendResult.getCnt_ord2dlv_late() - weeklyResult.getCnt_ord2dlv_late();
            }else{
                log.error("WeeklyResult(Delta)'s resultType is not valid:" + resultType);
            }
        }

        List<CategoryRow> categoryDlvResults = sdcMapper.selectCategoryDlvResults();
        for(CategoryRow categoryRow : categoryDlvResults){
            if(categoryRow.getSeq() <= 5){
                this.head_late_dlv_ctgr.add(categoryRow);
            }else{
                this.tail_late_dlv_ctgr.add(categoryRow);
            }
        }
    }

    public void setPredictionVariables(){
        // Overall prediction accuracy of last week
        this.pred_accuracy = sdcMapper.selectOverallPredictionResult();

        // prediction accuracy of date
        List<PredictionByDate> result = sdcMapper.selectPredictionResultsByDate();
        for(PredictionByDate predictionByDate : result){
            this.pred_acc_by_date.add(predictionByDate);
            this.pred_cnt_early += predictionByDate.getEarly_cnt();
            this.pred_cnt_exact += predictionByDate.getExact_cnt();
            this.pred_cnt_late += predictionByDate.getLate_cnt();
        }

        // Prediction Accuracy by Category
        List<CategoryRow> categoryPredResults = sdcMapper.selectPredictionResultsByCategory();
        for(CategoryRow categoryRow : categoryPredResults) {
            if(categoryRow.getSeq() <= 5){
                this.pred_head_acc_ctgr.add(categoryRow);
            }else{
                this.pred_tail_acc_ctgr.add(categoryRow);
            }
        }

        // Prediction Accuracy by Region
        List<CategoryRow> regionPredResults = sdcMapper.selectPredictionResultsByRegion();
        for(CategoryRow categoryRow : regionPredResults){
            if(categoryRow.getSeq() <= 5){
                this.pred_head_acc_region.add(categoryRow);
            }else{
                this.pred_tail_acc_region.add(categoryRow);
            }
        }
    }

    public void reportToSlack(){
        StringBuffer msg = new StringBuffer();
        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        msg.append("*** " + sdformat.format(date) + " 의 배송 기본 지표 BoT (지난 주 일~토 배송일 분) ***\n");
        msg.append(" [참고: 주중 주문(일~목), 주말 주문(금~토)]\n");
        msg.append(" 1. 배송 완료 건수(주중 주문)? [" + weekdayResult.getOrd_cnt() + "]\n");
        msg.append(" 2. 배송 완료 건수(주말 주문)? [" + weekendResult.getOrd_cnt() + "]\n");
        msg.append(" 3. 결제->발송 시간(주중 주문)? [" + formatFloat(weekdayResult.getAvg_ord2snd_hr()) + " hr]\n");
        msg.append(" 4. 결제->발송 시간(주말 주문)? [" + formatFloat(weekendResult.getAvg_ord2snd_hr()) + " hr]\n");
        msg.append(" 5. 결제->배송 시간(주중 주문)? [" + formatFloat(weekdayResult.getAvg_ord2dlv_hr()) + " hr]\n");
        msg.append(" 6. 결제->배송 시간(주말 주문)? [" + formatFloat(weekendResult.getAvg_ord2dlv_hr()) + " hr]\n");
        msg.append(" 7. 5일 이상 소요 배송 건수(주중 주문)? [" + weekdayResult.getCnt_ord2dlv_late() + " (" + formatFloat((float)weekdayResult.getCnt_ord2dlv_late() / (float)weekdayResult.getOrd_cnt() * 100.0) +"%)]\n");
        msg.append(" 8. 5일 이상 소요 배송 건수는(주말 주문)? [" + weekendResult.getCnt_ord2dlv_late() + " (" + formatFloat((float)weekendResult.getCnt_ord2dlv_late() / (float)weekendResult.getOrd_cnt() * 100.0) +"%)]\n");
        msg.append(" \n*** 2주전과 비교한 결과 ***\n");
        msg.append("1. 결제->발송 시간 변화(주중 주문)? [" + formatFloat(this.delta_ord_to_send_weekday_of_two_weeks) + " hr]\n");
        msg.append("2. 결제->발송 시간 변화(주말 주문)? [" + formatFloat(this.delta_ord_to_send_weekend_of_two_weeks) + " hr]\n");
        msg.append("3. 결제->배송 시간 변화(주중 주문)? [" + formatFloat(this.delta_ord_to_dlv_weekday_of_two_weeks) + " hr]\n");
        msg.append("4. 결제->배송 시간 변화(주말 주문)? [" + formatFloat(this.delta_ord_to_dlv_weekend_of_two_weeks) + " hr]\n");
        msg.append("5. 5일 이상 소요 배송 건수 변화(주중 주문)? [" + this.delta_over_5days_to_dlv_weekday_of_two_weeks + "]\n");
        msg.append("6. 5일 이상 소요 배송 건수 변화(주말 주문)? [" + this.delta_over_5days_to_dlv_weekend_of_two_weeks + "]\n");
        msg.append(" \n*** 기타 지표 ***\n");
        msg.append("1. 배송 느린 카테고리 하위 5위\n");
        for(CategoryRow vo : this.tail_late_dlv_ctgr){
            msg.append("\t[" + vo.getSeq() + "] " + vo.getCtgr_nm() + ", 평균 " + formatFloat(vo.getAvg_dlv_time()) + " 시간, " + vo.getCnt_dlv() + " 건\n");
        }
        msg.append("2. 배송 빠른 카테고리 상위 5위\n");
        for(CategoryRow vo : this.head_late_dlv_ctgr){
            msg.append("\t[" + vo.getSeq() + "] " + vo.getCtgr_nm() + ", 평균 " + formatFloat(vo.getAvg_dlv_time()) + " 시간, " + vo.getCnt_dlv() + " 건\n");
        }

        msg.append(" \n*** 배송 예측 결과 지표 ***\n");
        msg.append("1. 예측 정확도((예측일 도착 + 예측일 이전 도착)/배송 완료)? [" + formatFloat(this.pred_accuracy) + " %]\n");
        long pred_cnt_all = this.pred_cnt_early + this.pred_cnt_exact + this.pred_cnt_late;
        msg.append("2. 예측일 도착 건수? [" + this.pred_cnt_exact + " (" + formatFloat((float)this.pred_cnt_exact / (float)pred_cnt_all * 100) + " %)]\n");
        msg.append("3. 예측일 이전 도착 건수? [" + this.pred_cnt_early + " (" + formatFloat((float)this.pred_cnt_early / (float)pred_cnt_all * 100) + " %)]\n");
        msg.append("4. 예측일 이후 도착(예측 실패) 건수? [" + this.pred_cnt_late + " (" + formatFloat((float)this.pred_cnt_late / (float)pred_cnt_all * 100) + " %)]\n");
        msg.append("5. 도착 날짜별 예측 정확도\n");
        for(PredictionByDate vo : this.pred_acc_by_date){
            long early = vo.getEarly_cnt();
            long exact = vo.getExact_cnt();
            long late = vo.getLate_cnt();
            msg.append("\t[" + vo.getDlv_end_dt() + "] " + formatFloat((float)(early + exact) / (float)(early + exact + late) * 100) + " %" + ", early(" + early + "), exact(" + exact + "), late(" + late + ")\n");
        }
        msg.append("6. 예측 정확도 상위 5개 카테고리\n");
        for(CategoryRow vo : this.pred_head_acc_ctgr){
            msg.append("\t[" + vo.getSeq() + "] " + vo.getCtgr_nm() + ", 정확도 " + formatFloat(vo.getAvg_dlv_time()) + " %, " + vo.getCnt_dlv() + " 건\n");
        }
        msg.append("7. 예측 정확도 하위 5개 카테고리\n");
        for(CategoryRow vo : this.pred_tail_acc_ctgr){
            msg.append("\t[" + vo.getSeq() + "] " + vo.getCtgr_nm() + ", 정확도 " + formatFloat(vo.getAvg_dlv_time()) + " %, " + vo.getCnt_dlv() + " 건\n");
        }
        msg.append("8. 예측 정확도 상위 5개 지역\n");
        for(CategoryRow vo : this.pred_head_acc_region){
            msg.append("\t[" + vo.getSeq() + "] " + vo.getCtgr_nm() + ", 정확도 " + formatFloat(vo.getAvg_dlv_time()) + " %, " + vo.getCnt_dlv() + " 건\n");
        }
        msg.append("9. 예측 정확도 하위 5개 지역\n");
        for(CategoryRow vo : this.pred_tail_acc_region){
            msg.append("\t[" + vo.getSeq() + "] " + vo.getCtgr_nm() + ", 정확도 " + formatFloat(vo.getAvg_dlv_time()) + " %, " + vo.getCnt_dlv() + " 건\n");
        }
        SlackTalker.talk(settings.SLACK_ENDPOINT_SDC, "```" + msg.toString() + "```");
    }

    public void reportToSplunk(){
        SplunkSender sender = new SplunkSender();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("source", "omdlv");
        paramMap.put("time", new Date().getTime());
        try {
            paramMap.put("host", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("Prediction_Accuracy", formatFloat(this.pred_accuracy));
        eventMap.put("Exact_Arrival", this.pred_cnt_exact);
        eventMap.put("Earlier_than_Prediction", this.pred_cnt_early);
        eventMap.put("Later_than_Prediction", this.pred_cnt_late);
        eventMap.put("Category_Accuracy_Top_5", this.pred_head_acc_ctgr);
        eventMap.put("Category_Accuracy_Bottom_5", this.pred_tail_acc_ctgr);
        eventMap.put("Region_Accuracy_Top_5", this.pred_head_acc_region);
        eventMap.put("Region_Accuracy_Bottom_5", this.pred_tail_acc_region);
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