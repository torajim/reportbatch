package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class My11WeeklyAccuracy {
    private String fromdate;
    private String todate;
    private long total;
    private long predictable;
    private long early;
    private long exact;
    private long late;
    private float coverage;
    private float precision;
}