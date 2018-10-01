package com.skplanet.dpa.reportbatch.domain.hotclkad.repository;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CtrRps {
    private float oldCtr;
    private float v1Ctr;
    private float v2Ctr;
    private float oldRps;
    private float v1Rps;
    private float v2Rps;
}