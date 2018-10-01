package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@BIDB
public interface SDCMy11Mapper {
    public My11MonthlyAccuracy selectMy11MonthlyAccuracy(int addMonVal);
    public My11MonthlyProductCoverage selectMy11MonthlyProductCoverage(int addMonVal);
    public My11WeeklyAccuracy selectMy11WeeklyAccuracy(int addWeekVal);
    public My11WeeklyProductCoverage selectMy11WeeklyProductCoverage(int addWeekVal);
}