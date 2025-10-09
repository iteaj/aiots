package com.iteaj.iboot.plugin.iotview;

import com.iteaj.framework.result.Result;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchResult extends IotViewResult {

    private String chart;

    protected FetchResult(String chart, Result result) {
        super(result);
        this.chart = chart;
    }

    public static FetchResult builder(String chart, Result result) {
        return new FetchResult(chart, result);
    }
}
