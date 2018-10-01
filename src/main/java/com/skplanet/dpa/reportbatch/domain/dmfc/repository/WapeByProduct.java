package com.skplanet.dpa.reportbatch.domain.dmfc.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

/**
 * Splunk 하위 호환 때문에, 변수 명 변경하면 안됨.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WapeByProduct implements Comparable{
    private long asc_rank;
    private long desc_rank;
    private long prd_no;
    private long stck_no;
    private String prd_nm;
    private long ord_qty;
    private float predtn_qty;
    private float error_ratio;

    public int compareTo(@SuppressWarnings("NullableProblems") Object o) {
        int compareResult = -9;
        if(o instanceof WapeByProduct){
            WapeByProduct vo = (WapeByProduct)o;
            if(this.asc_rank == vo.getAsc_rank()){
                compareResult = 0;
            }else if(this.getAsc_rank() > vo.getAsc_rank()){
                compareResult = 1;
            }else if(this.getAsc_rank() < vo.getAsc_rank()){
                compareResult = -1;
            }
        }else{
            throw new IllegalArgumentException();
        }
        return compareResult;
    }
}