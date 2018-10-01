package com.skplanet.dpa.reportbatch.domain.sellerpcs.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChartViewCtlg {
    private long chartViewCtlgNo;
    private String dispModelNm;
    private long rnk;
    private long chartView;
}
