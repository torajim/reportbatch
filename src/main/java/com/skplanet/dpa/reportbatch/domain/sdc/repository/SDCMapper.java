package com.skplanet.dpa.reportbatch.domain.sdc.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@BIDB
public interface SDCMapper {
    public List<WeeklyResult> selectWeeklyResults();
    public List<WeeklyResult> selectOldWeeklyResults();
    public List<CategoryRow> selectCategoryDlvResults();
    public Float selectOverallPredictionResult();
    public List<PredictionByDate> selectPredictionResultsByDate();
    public List<CategoryRow> selectPredictionResultsByCategory();
    public List<CategoryRow> selectPredictionResultsByRegion();
}