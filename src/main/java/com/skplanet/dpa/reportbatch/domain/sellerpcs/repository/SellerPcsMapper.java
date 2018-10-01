package com.skplanet.dpa.reportbatch.domain.sellerpcs.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@BIDB
public interface SellerPcsMapper {
    public DailyStat selectDailyStat(int addDayVal);
    public MonthlyStat selectMonthlyStat(int addDayVal);
    public List<ChartViewCtlg> selectChartViewCtlgTop10(int addDayVal);
    public SoMainStat selectSoMainStat(int addDayVal);
}