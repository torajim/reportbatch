package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 1003724 on 2017-11-06.
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PredictionByDate implements Comparable{
    private String dlv_end_dt;
    private long exact_cnt;
    private long early_cnt;
    private long late_cnt;

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Object o) {
        int compareResult = -9;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if(o instanceof PredictionByDate){
            PredictionByDate vo = (PredictionByDate)o;
            try {
                Date org_date = sdf.parse(this.dlv_end_dt);
                Date new_date = sdf.parse(vo.getDlv_end_dt());
                compareResult = org_date.compareTo(new_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            throw new IllegalArgumentException();
        }
        return compareResult;
    }
}