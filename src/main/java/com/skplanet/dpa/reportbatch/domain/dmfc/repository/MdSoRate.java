package com.skplanet.dpa.reportbatch.domain.dmfc.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MdSoRate {
    private String mdName;
    private long sellCnt;
    private long soCnt;
    private float soRate;
}