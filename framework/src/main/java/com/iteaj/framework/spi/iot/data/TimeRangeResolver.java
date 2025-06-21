package com.iteaj.framework.spi.iot.data;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.consts.TimeCountType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class TimeRangeResolver {

    private String startTime;

    private String endTime;

    private List<BaseEchartsCount> emptyCounts = new ArrayList<>();

    public TimeRangeResolver(TimeCountType timeType, List<String> timeRange) {
        String[] result = new String[]{timeRange.get(0), timeRange.get(1)};
        DateTime startTime, endTime;
        int startOffset = 0, endOffset = 0;
        if(result[0].trim().equalsIgnoreCase("now")) {
            startTime = new DateTime();
        } else if(result[0].contains("now")) {
            startTime = new DateTime();
            String replaceAll = result[0].replaceAll(" ", "");
            if(replaceAll.contains("+")) {
                startOffset = 0 + Integer.valueOf(replaceAll.substring(4));
            } else if(replaceAll.contains("-")) {
                startOffset = 0 - Integer.valueOf(replaceAll.substring(4));
            } else {
                throw new ServiceException("不支持的时间操作符");
            }
        } else {
            startTime = DateUtil.parse(result[0], "yyyy-MM-dd HH:mm:ss");
        }

        if(result[1].trim().equalsIgnoreCase("now")) {
            endTime = new DateTime(); endOffset = endOffset + 1;
        } else if(result[1].contains("now")) {
            endTime = new DateTime();
            String replaceAll = result[1].replaceAll(" ", "");
            if(replaceAll.contains("+")) {
                endOffset = 1 + Integer.valueOf(replaceAll.substring(4));
            } else if(replaceAll.contains("-")) {
                endOffset = 1 - Integer.valueOf(replaceAll.substring(4));
            } else {
                throw new ServiceException("不支持的时间操作符");
            }
        } else {
            endOffset = endOffset + 1;
            endTime = DateUtil.parse(result[1], "yyyy-MM-dd HH:mm:ss");
        }

        if(timeType == TimeCountType.year) {
            DateTime startDateTime = startTime.offset(DateField.YEAR, startOffset).setField(DateField.MONTH, 0)
                    .setField(DateField.DAY_OF_MONTH, 1).setField(DateField.HOUR_OF_DAY, 0)
                    .setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0)
                    .setField(DateField.MILLISECOND, 0);
            result[0] = "" + startDateTime.getTime();
            DateTime endDateTime = endTime.offset(DateField.YEAR, endOffset).setField(DateField.MONTH, 0)
                    .setField(DateField.DAY_OF_MONTH, 1).setField(DateField.HOUR_OF_DAY, 0)
                    .setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0)
                    .setField(DateField.MILLISECOND, 0);
            result[1] = "" + (endDateTime.getTime() - 1);
            DateTime tempDate = startDateTime;
            while (tempDate.getTime() <= (endDateTime.getTime() - 1)) {
                String time = tempDate.toString("yyyy");
                emptyCounts.add(new BaseEchartsCount(time, 0));
                tempDate = tempDate.offset(DateField.YEAR, 1);
            }
        } else if(timeType == TimeCountType.quarter) {

        } else if(timeType == TimeCountType.month) {
            DateTime startDateTime = startTime.offset(DateField.MONTH, startOffset)
                    .setField(DateField.DAY_OF_MONTH, 1).setField(DateField.HOUR_OF_DAY, 0)
                    .setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0)
                    .setField(DateField.MILLISECOND, 0);
            result[0] = "" + startDateTime.getTime();
            DateTime endDateTime = endTime.offset(DateField.MONTH, endOffset)
                    .setField(DateField.DAY_OF_MONTH, 1).setField(DateField.HOUR_OF_DAY, 0)
                    .setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0)
                    .setField(DateField.MILLISECOND, 0);
            result[1] = "" + (endDateTime.getTime() - 1);
            DateTime tempDate = startDateTime;
            while (tempDate.getTime() <= (endDateTime.getTime() - 1)) {
                String time = tempDate.toString("yyyy-MM");
                emptyCounts.add(new BaseEchartsCount(time, 0));
                tempDate = tempDate.offset(DateField.MONTH, 1);
            }
        } else if(timeType == TimeCountType.week) {
            DateTime startDateTime = startTime.offset(DateField.WEEK_OF_MONTH, startOffset)
                    .setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0)
                    .setField(DateField.SECOND, 0).setField(DateField.MILLISECOND, 0);
            result[0] = "" + startDateTime.getTime();
            DateTime endDateTime = endTime.offset(DateField.WEEK_OF_MONTH, endOffset)
                    .setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0)
                    .setField(DateField.SECOND, 0).setField(DateField.MILLISECOND, 0);
            result[1] = "" + (endDateTime.getTime() - 1);
            DateTime tempDate = startDateTime;
            while (tempDate.getTime() <= (endDateTime.getTime() - 1)) {
                String time = tempDate.toString("yyyy-MM-dd");
                emptyCounts.add(new BaseEchartsCount(time, 0));
                tempDate = tempDate.offset(DateField.DAY_OF_MONTH, 1);
            }
        } else if(timeType == TimeCountType.day) {
            DateTime startDateTime = startTime.offset(DateField.DAY_OF_MONTH, startOffset)
                    .setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0)
                    .setField(DateField.SECOND, 0).setField(DateField.MILLISECOND, 0);
            result[0] = "" + startDateTime.getTime();
            DateTime endDateTime = endTime.offset(DateField.DAY_OF_MONTH, endOffset)
                    .setField(DateField.HOUR_OF_DAY, 0).setField(DateField.MINUTE, 0)
                    .setField(DateField.SECOND, 0).setField(DateField.MILLISECOND, 0);
            result[1] = "" + (endDateTime.getTime() - 1);
            DateTime tempDate = startDateTime;
            while (tempDate.getTime() <= (endDateTime.getTime() - 1)) {
                String time = tempDate.toString("yyyy-MM-dd");
                emptyCounts.add(new BaseEchartsCount(time, 0));
                tempDate = tempDate.offset(DateField.DAY_OF_MONTH, 1);
            }
        } else if(timeType == TimeCountType.hour) {
            DateTime startDateTime = startTime.offset(DateField.HOUR_OF_DAY, startOffset)
                    .setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0)
                    .setField(DateField.MILLISECOND, 0);
            result[0] = "" + startDateTime.getTime();
            DateTime endDatetime = endTime.offset(DateField.HOUR_OF_DAY, endOffset)
                    .setField(DateField.MINUTE, 0).setField(DateField.SECOND, 0)
                    .setField(DateField.MILLISECOND, 0);
            result[1] = "" + (endDatetime.getTime() - 1);
            DateTime tempDate = startDateTime;
            while (tempDate.getTime() <= (endDatetime.getTime() - 1)) {
                String time = tempDate.toString("yyyy-MM-dd HH");
                emptyCounts.add(new BaseEchartsCount(time, 0));
                tempDate = tempDate.offset(DateField.HOUR_OF_DAY, 1);
            }
        } else {
            throw new ServiceException("不支持的时间类型["+timeType+"]");
        }

        this.startTime = result[0]; this.endTime = result[1];
    }

    public List<BaseEchartsCount> buildValues(List<BaseEchartsCount> counts) {
        List<BaseEchartsCount> result = emptyCounts;
        if(CollectionUtil.isNotEmpty(counts)) {
            result = counts;
            Map<String, BaseEchartsCount> timeCountMap = counts.stream()
                    .collect(Collectors.toMap(item -> item.getCategory(), item -> item));
            for (int i = 0; i < this.getEmptyCounts().size(); i++) {
                BaseEchartsCount emptyTimeCount = this.getEmptyCounts().get(i);
                if(!timeCountMap.containsKey(emptyTimeCount.getCategory())) {
                    result.add(i, emptyTimeCount);
                }
            }
        }

        return result;
    }
}
