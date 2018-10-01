package com.skplanet.dpa.reportbatch.domain.dex.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@BIDB
public interface DexMapper {
    public DailyQaReport selectDailyQaReport(int addDayVal);
}