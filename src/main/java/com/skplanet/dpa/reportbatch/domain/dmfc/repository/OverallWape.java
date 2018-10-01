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
public class OverallWape {
    private int resultType;              // 0: 전체, 1: 예측보다 실제가 적은 경우, 2: 예측보다 실제가 같거나 많은 경우
    private long stckCnt;                // 예측 판매 SKU 수
    private long realOrdQty;             // 실제 주문 수량
    private float predOrdQty;            // 예측 주문 수량
    private float wape;                  // WAPE
}