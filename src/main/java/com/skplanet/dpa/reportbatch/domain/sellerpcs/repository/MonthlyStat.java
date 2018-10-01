package com.skplanet.dpa.reportbatch.domain.sellerpcs.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MonthlyStat {
    private long mau;
    private long totSelCnt;            // Total Seller Count
}