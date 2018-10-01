package com.skplanet.dpa.reportbatch.domain.dex.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DailyQaReport {
    private String baseDt;
    private long total;
    private long impression;
    private long notImpression;
    private long impressionHealthy;
    private long impressionMaybeHealthy;
    private long impressionUnhealthy;
    private long notImpressionHealthy;
    private long notImpressionMaybeHealthy;
    private long notImpressionUnhealthy;
    private float unhealthyRate;
    private float impressionUnhealthyRate;
    private float notImpressionUnhealthyRate;
}
