package com.skplanet.dpa.reportbatch.domain.dmfc.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@BIDB
public interface DmfcMapper {
    public List<OverallWape> selectOverallWape();
    public List<WapeByPredModel> selectPredModelWape();
    public List<WapeByProduct> selectProductWape();
}