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
public class WapeByPredModel {
    private String predAlgClfCd;
    private String algName;
    private long stckCnt;
    private long realOrdQty;
    private float predOrdQty;
    private float wape;
}