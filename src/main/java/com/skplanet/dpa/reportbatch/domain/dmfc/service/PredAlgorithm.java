package com.skplanet.dpa.reportbatch.domain.dmfc.service;

public enum PredAlgorithm {
    moving_average("00001"),
    arima("00002");

    private final String value;

    PredAlgorithm(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static PredAlgorithm codeOf(String value){
        switch(value){
            case "00001": return PredAlgorithm.moving_average;
            case "00002": return PredAlgorithm.arima;
            default: throw new AssertionError("value is not acceptable:" + value);
        }
    }
}