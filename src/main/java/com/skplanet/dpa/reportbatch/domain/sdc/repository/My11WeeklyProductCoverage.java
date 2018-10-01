package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class My11WeeklyProductCoverage {
    private String fromdate;
    private String todate;
    private long prdTotal;
    private long prdPred;
    private float coverage;
}
