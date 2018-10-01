package com.skplanet.dpa.reportbatch.domain.dmfc.service;

public enum WapeCategory {
    all(0),
    less_than_pred(1),
    greater_than_or_equal_to_pred(2);

    private final int value;

    WapeCategory(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static WapeCategory codeOf(int value){
        switch(value){
            case 0: return WapeCategory.all;
            case 1: return WapeCategory.less_than_pred;
            case 2: return WapeCategory.greater_than_or_equal_to_pred;
            default: throw new AssertionError("value is not acceptable:" + value);
        }
    }
}