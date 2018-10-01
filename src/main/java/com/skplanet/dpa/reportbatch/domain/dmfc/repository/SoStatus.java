package com.skplanet.dpa.reportbatch.domain.dmfc.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SoStatus {
    private long prdNo;
    private long stckNo;
    private String prdName;
    private String mdName;
    private long stckQty;      // 가용재고
    private long ordrQty;      // 기발주
    private long waitStckQty;  // 적치대기
    private long wkSelQty1;    // 최근 1주 판매량
    private long wkSelQty2;    // 최근 2주 판매량
    private long wkSelQty4;    // 최근 4주 판매량
    private String selStat;    // 판매상태
    private String displayYn;   // 전시상태
    private String dstStckStat; // 재고속성
    private String dlvClf;      // 배송유형
    private long soPeriod;      // 품절기간
    private String centerName;  // 센터명
}
