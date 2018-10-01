package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class My11MonthlyProductCoverage {
    private String fromdate;
    private String todate;
    private long prdTotal;
    private long prdPred;
    private float coverage;
}
