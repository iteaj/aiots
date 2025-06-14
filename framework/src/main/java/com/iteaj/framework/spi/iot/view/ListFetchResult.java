package com.iteaj.framework.spi.iot.view;

import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListFetchResult extends ViewFetchResult {

    public ListFetchResult(String item, List<BaseEchartsCount> values) {
        super(item, values);
    }

    @Override
    public List<BaseEchartsCount> getValue() {
        return (List<BaseEchartsCount>) super.getValue();
    }
}
