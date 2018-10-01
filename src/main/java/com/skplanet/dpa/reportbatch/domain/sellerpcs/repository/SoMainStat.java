package com.skplanet.dpa.reportbatch.domain.sellerpcs.repository;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SoMainStat {
    private String dateStr;
    private long SoMainPv;
    private long SoMainSelCnt;
}
