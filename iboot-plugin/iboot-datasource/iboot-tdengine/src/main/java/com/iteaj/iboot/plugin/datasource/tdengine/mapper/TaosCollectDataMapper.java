package com.iteaj.iboot.plugin.datasource.tdengine.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.iot.consts.TimeCountCondition;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.CollectDataCondition;
import com.iteaj.framework.spi.iot.data.DefaultRealtimeData;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.iboot.plugin.datasource.tdengine.listener.TaosRealtimeData;
import com.iteaj.framework.spi.iot.data.IndicatorStats;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

import static com.iteaj.iboot.plugin.datasource.tdengine.mapper.TaosCollectDataMapper.DS_NAME;

@DS(DS_NAME)
public interface TaosCollectDataMapper {

    String DS_NAME = "tdengine";

    Page<RealtimeCollectData> detailOfPage(Page page, RealtimeCollectData entity);

    /**
     * 统计最近一周的数据
     * @param deviceGroupId
     * @return
     */
    List<BaseEchartsCount> countLastWeek(Long deviceGroupId);

    /**
     * 统计最近指定天的数据
     * @param days 天数
     * @return
     */
    List<BaseEchartsCount> countLastByDays(int days);

    /**
     * 通用的时间统计
     * @return
     * @param item
     */
    List<BaseEchartsCount> countOfCommonByTime(TimeCountCondition.TimeCountSqlCondition item);

    /**
     * 通用的时间列表
     * @param item
     * @return
     */
    List<RealtimeCollectData> listOfCommonByTime(CollectDataCondition.DataSqlCondition item);

    Page pageOfCommonByTime(Page<RealtimeCollectData> page, CollectDataCondition.DataSqlCondition condition);

    Page<RealtimeCollectData> productOfPage(Page page, RealtimeCollectData entity);

    List<DefaultRealtimeData> listProductData(List<RealtimeCollectData> list);

    /**
     * 获取点位聚合统计
     */
    IndicatorStats statsOfField(@Param("uid") String uid, @Param("fieldId") Long fieldId,
                                @Param("start") String start, @Param("end") String end);

    /**
     * 获取点位采集数据列表
     */
    List<DefaultRealtimeData> listOfData(@Param("uid") String uid, @Param("fieldId") Long fieldId,
                                         @Param("start") String start, @Param("end") String end);

    /**
     * 获取设备其他点位同采集时间数据
     */
    List<DefaultRealtimeData> associatedPoints(@Param("uid") String uid, @Param("excludeFieldId") Long excludeFieldId,
                                               @Param("collectTime") String collectTime);

    /**
     * 获取同产品其他设备同点位数据
     */
    List<DefaultRealtimeData> crossDevicePoints(@Param("productCode") String productCode, @Param("fieldId") Long fieldId,
                                                @Param("collectTime") String collectTime, @Param("excludeUid") String excludeUid);

    /**
     * 获取源点位
     */
    DefaultRealtimeData getSourcePoint(@Param("uid") String uid, @Param("fieldId") Long fieldId,
                                       @Param("collectTime") String collectTime);

    /**
     * 按采集器查询关联点位，ROW_NUMBER去重
     */
    List<DefaultRealtimeData> listByCid(@Param("cid") String cid, @Param("excludeUid") String excludeUid,
                                        @Param("excludeFieldId") Long excludeFieldId, @Param("grade") String grade);

    /**
     * 按网关或设备分组查询关联点位，ROW_NUMBER去重
     */
    List<DefaultRealtimeData> listByPuidOrGroup(@Param("puid") String puid, @Param("deviceGroupId") Long deviceGroupId,
                                                @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                @Param("excludeUid") String excludeUid, @Param("excludeFieldId") Long excludeFieldId,
                                                @Param("grade") String grade);
}
