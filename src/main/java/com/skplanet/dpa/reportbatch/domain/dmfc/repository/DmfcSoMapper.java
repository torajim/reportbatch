package com.skplanet.dpa.reportbatch.domain.dmfc.repository;

import static com.skplanet.dpa.reportbatch.common.config.BIDBConfig.BIDB;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@BIDB
public interface DmfcSoMapper {
    public List<MdSoRate> selectMdSoRates();
    public List<SoStatus> selectSoStatusDeliveredBy11st();
    public List<SoStatus> selectSoStatusDeliveredBySeller();
    public List<SoPrediction> selectSoPredictions();
    public List<MdProduct> selectMdProducts();
}
