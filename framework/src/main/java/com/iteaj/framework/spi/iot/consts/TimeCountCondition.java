package com.iteaj.framework.spi.iot.consts;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.data.BaseCondition;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.EchartsCountValue;
import com.iteaj.framework.spi.iot.data.TimeRangeResolver;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 时间统计条件
 */
@Getter
@Setter
public class TimeCountCondition extends BaseCondition {

    /**
     * 计算类型
     */
    private CountCalcType calcType = CountCalcType.count;

    private List<BaseEchartsCount> timeEmptyValue;

    public List<TimeCountSqlCondition> buildSqlConditions() {
        validateCondition();
        List<TimeCountSqlCondition> conditions = new ArrayList<>();
        List<BaseCondition.ValueMeta> resolverValue = resolverValue(this.getValue());
        final TimeRangeResolver resolver = new TimeRangeResolver(this.getTimeType(), this.getTimeRange());
        timeEmptyValue = resolver.getEmptyCounts();
        if(CollectionUtil.isNotEmpty(resolverValue)) {
            resolverValue.forEach(meta -> {
                TimeCountSqlCondition condition = new TimeCountSqlCondition(this.getTimeType()
                , this.calcType, resolver.getStartTime(), resolver.getEndTime());
                if(meta.getType().equals("D")) {
                    conditions.add(condition.buildDevice(meta.getValue(), meta.getFields()));
                } else if(meta.getType().equals("P")) {
                    conditions.add(condition.buildProduct(meta.getValue(), meta.getFields()));
                } else {
                    conditions.add(condition.buildFields(meta.getFields()));
                }
            });
        } else {
            conditions.add(new TimeCountSqlCondition(this.getTimeType(), this.calcType, resolver.getStartTime(), resolver.getEndTime()));
        }

        return conditions;
    }

    public List<EchartsCountValue> buildResult(TimeCountSqlCondition condition, List<BaseEchartsCount> counts) {
        List<EchartsCountValue> result = new ArrayList<>();
        if(CollectionUtil.isEmpty(counts)) {
            if(CollectionUtil.isNotEmpty(condition.fields)) {
                condition.fields.forEach(field -> {
                    result.add(new EchartsCountValue(condition.uid, field, this.timeEmptyValue));
                });
            } else {
                result.add(new EchartsCountValue(condition.uid, this.timeEmptyValue));
            }
        } else {
            if(CollectionUtil.isNotEmpty(condition.fields)) {
                Map<String, List<BaseEchartsCount>> fieldGroupCount = counts.stream()
                        .collect(Collectors.groupingBy(BaseEchartsCount::getField));
                condition.fields.forEach(item -> {
                    List<BaseEchartsCount> baseEchartsCounts = fieldGroupCount.get(item);
                    if(baseEchartsCounts == null) {
                        result.add(new EchartsCountValue(condition.uid, item, this.timeEmptyValue));
                    } else {
                        Map<String, BaseEchartsCount> timeCountMap = baseEchartsCounts.stream()
                                .collect(Collectors.toMap(item1 -> item1.getCategory(), item1 -> item1));
                        for (int i = 0; i < timeEmptyValue.size(); i++) {
                            BaseEchartsCount emptyTimeCount = timeEmptyValue.get(i);
                            if(!timeCountMap.containsKey(emptyTimeCount.getCategory())) {
                                baseEchartsCounts.add(i, emptyTimeCount);
                            }
                        }
                        result.add(new EchartsCountValue(condition.uid, item, baseEchartsCounts));
                    }
                });
            } else {
                Map<String, BaseEchartsCount> timeCountMap = counts.stream()
                        .collect(Collectors.toMap(item -> item.getCategory(), item -> item));
                for (int i = 0; i < timeEmptyValue.size(); i++) {
                    BaseEchartsCount emptyTimeCount = timeEmptyValue.get(i);
                    if(!timeCountMap.containsKey(emptyTimeCount.getCategory())) {
                        counts.add(i, emptyTimeCount);
                    }
                }

                result.add(new EchartsCountValue(condition.uid, null, counts));
            }
        }

        return result;
    }

    @Data
    public static class TimeCountSqlCondition {
        private String uid;
        private String productCode;
        private List<String> fields;
        private String timeType;
        private String calcType;
        private String startTimestamp;
        private String endTimestamp;

        public TimeCountSqlCondition(String uid, List<String> fields, TimeCountType timeType
        , CountCalcType calcType, String startTimestamp, String endTimestamp) {
            this.uid = uid;
            this.fields = fields;
            this.timeType = timeType.name();
            this.calcType = calcType.name();
            this.startTimestamp = startTimestamp;
            this.endTimestamp = endTimestamp;
        }

        public TimeCountSqlCondition(TimeCountType timeType, CountCalcType calcType, String startTimestamp, String endTimestamp) {
            this.timeType = timeType.name();
            this.calcType = calcType.name();
            this.startTimestamp = startTimestamp;
            this.endTimestamp = endTimestamp;
        }

        public TimeCountSqlCondition buildFields(List<String> fields) {
            this.fields = fields;
            return this;
        }

        public TimeCountSqlCondition buildDevice(String uid, List<String> fields) {
            this.uid = uid;
            this.fields = fields;
            return this;
        }

        public TimeCountSqlCondition buildProduct(String productCode, List<String> fields) {
            this.productCode = productCode;
            this.fields = fields;
            return this;
        }
    }
}
