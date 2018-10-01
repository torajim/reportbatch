package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyResult {
    private int ord_type;            // 0: weekday, 1: weekend
    private long ord_cnt;
    private float avg_ord2snd_hr;
    private float med_ord2snd_hr;
    private float avg_ord2dlv_hr;
    private float med_ord2dlv_hr;
    private long cnt_ord2dlv_late;
}