package com.iteaj.framework.spi.iot;

import java.util.ArrayList;
import java.util.List;

public class BatchUtils {

    public static List<List<BatchOfPoint>> splitNumAddress(List<BatchOfPoint> points, int limit) {
        List<List<BatchOfPoint>> result = new ArrayList<>();
        if(points.size() == 1) {
            result.add(points);
        } else {
            int start = Integer.parseInt(points.get(0).getAddress());
            int end = Integer.parseInt(points.get(points.size() - 1).getAddress());
            if((end - start) < limit) {
                result.add(points);
            } else {
                int startIndex = 0;
                for (int i = 1; i < points.size(); i++) {
                    final int i1 = Integer.parseInt(points.get(i).getAddress());
                    if((i1 - start) > limit) {
                        final List<BatchOfPoint> subList = points.subList(startIndex, i);
                        result.add(subList);
                        if(i == (points.size() - 1)) { // 已经是最后一个
                            result.add(points.subList(i, points.size()));
                        } else { // 继续解析
                            start = i1;
                            startIndex = i;
                        }
                    } else if(i == (points.size() - 1)) {
                        result.add(points.subList(startIndex, points.size()));
                    }
                }
            }
        }

        return result;
    }
}
