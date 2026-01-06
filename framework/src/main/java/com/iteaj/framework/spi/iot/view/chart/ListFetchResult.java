package com.iteaj.framework.spi.iot.view.chart;

import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListFetchResult extends ChartFetchResult {

    public ListFetchResult(String item, List<BaseEchartsCount> values) {
        super(item, values);
    }

    public ListFetchResult addValue(BaseEchartsCount value) {
        getValue().add(value);
        return this;
    }

    @Override
    public List<BaseEchartsCount> getValue() {
        return (List<BaseEchartsCount>) super.getValue();
    }
}
