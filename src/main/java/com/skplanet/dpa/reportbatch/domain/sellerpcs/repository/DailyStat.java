package com.skplanet.dpa.reportbatch.domain.sellerpcs.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DailyStat {
    private long dau;                                // Daily Active User
    private long pv;                                 // Page View
    private float avgVf;                             // Average Visit Frequency
    private long prcSortClk;                         // Price Competitive Sorting Click Count
    private float avgSc;                             // Price Competitive Sorting Click Count / DAU
    private long chartView;                          // Price Trend Chart View Click Count
    private float avgCv;                             // Price Trend Chart View Click Count / DAU
    private long alarmSetupClk;                      // Alarm Setup Click Count
    private long acmAlarmSetupClk;                   // Accumulation of Alarm Setup Click Count
    private float avgAlarmSetupClk;                  // Alarm Setup Click Count / DAU
    private long prdAlarmOnClk;                      // Product Alarm On Click Count
    private long prdAlarmOffClk;                     // Product Alarm Off Click Count
    private long acmPrdAlarmOn;                      // Accumulation of Product Alarm On-Off Count
    private float avgPrdAlarmClk;                    // Product Alarm On/Off Click Count / DAU
    private long mngrSelCnt;                         // Seller Count Registered for E-mail Notification
    private long acmMngrSelCnt;                      // Accumulation of Seller Count Registered for E-mail Notification
    private long prdAdjClk;                          // Number of price changes
    private long prdAdjCnfmCnt;                      // Number of price successful changes
    private long prcAdjCnfmSelCnt;                   // Number of sellers who succeeded in price change
    private long prdAdjDscAmt;                       // Reduction amount
}