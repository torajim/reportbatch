package com.skplanet.dpa.reportbatch.domain.hotclkad.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@BIDB
public interface HotClkAdMapper {
    public CtrRps selectCtrRps(int addDayVal);
}