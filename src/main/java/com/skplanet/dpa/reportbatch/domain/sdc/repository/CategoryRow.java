package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

/**
 * Created by 1003724 on 2017-11-01.
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRow implements Comparable{
    private long seq;
    private String ctgr_nm;
    private float avg_dlv_time;
    private long cnt_dlv;

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Object o) {
        int compareResult = -9;
        if(o instanceof CategoryRow){
            CategoryRow vo = (CategoryRow)o;
            if(this.seq == vo.getSeq()){
                compareResult = 0;
            }else if(this.seq > vo.getSeq()){
                compareResult = 1;
            }else if(this.seq < vo.getSeq()){
                compareResult = -1;
            }
        }else{
            throw new IllegalArgumentException();
        }
        return compareResult;
    }
}