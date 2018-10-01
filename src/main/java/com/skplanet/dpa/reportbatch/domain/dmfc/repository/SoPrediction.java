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
public class SoPrediction {
    private long prdNo;
    private long stckNo;
    private String prdName;
    private String mdName;
    private long stckQty;             // 가용재고
    private long ordrQty;             // 기발주
    private long waitStckQty;         // 적치대기
    private long totalStckQty;        // 전체재고
    private long wkPreQty;          // D0-D6 예측 판매 수량
    private long wkSelQty1;           // 최근 1주 판매량
    private long wkSelQty2;           // 최근 2주 판매량
    private long wkSelQty4;           // 최근 4주 판매량
    private String selStat;           // 판매상태
    private String dstStckStat;       // 재고속성
    private String dlvClf;            // 배송유형
}