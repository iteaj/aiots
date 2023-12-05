package com.genersoft.iot.vmp.storager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genersoft.iot.vmp.gb28181.bean.DeviceChannel;
import com.genersoft.iot.vmp.gb28181.bean.GbStream;
import com.genersoft.iot.vmp.media.zlm.dto.StreamProxyItem;
import com.genersoft.iot.vmp.media.zlm.dto.StreamPushItem;
import com.genersoft.iot.vmp.service.bean.GPSMsgInfo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GbStreamMapper extends BaseMapper<GbStream> {

    default int updateByAppAndStream(GbStream gbStream) {
        return this.update(gbStream, Wrappers.<GbStream>lambdaUpdate()
                .eq(GbStream::getApp, gbStream.getApp())
                .eq(GbStream::getStream, gbStream.getStream()));
    }

    default int del(@Param("app") String app, @Param("stream") String stream) {
        return this.delete(Wrappers.<GbStream>lambdaQuery().eq(GbStream::getApp, app).eq(GbStream::getStream, stream));
    }

    List<GbStream> selectAll(GbStream entity);

    Page<GbStream> selectAllByPage(Page page, GbStream entity);

    default GbStream selectOne(@Param("app") String app, @Param("stream") String stream) {
        return this.selectOne(Wrappers.<GbStream>lambdaQuery().eq(GbStream::getApp, app).eq(GbStream::getStream, stream));
    }

    default List<GbStream> selectByGBId(String gbId) {
        return this.selectList(Wrappers.<GbStream>lambdaQuery().eq(GbStream::getGbId, gbId));
    }

    GbStream queryStreamInPlatform(@Param("platformId") String platformId, @Param("gbId") String gbId);

    List<DeviceChannel> queryGbStreamListInPlatform(String platformId, @Param("usPushingAsStatus") boolean usPushingAsStatus);

    default void deleteWithoutGBId(@Param("type") String type, @Param("mediaServerId") String mediaServerId) {
        this.delete(Wrappers.<GbStream>lambdaQuery()
                .eq(GbStream::getStreamType, type)
                .isNull(GbStream::getGbId)
                .eq(GbStream::getMediaServerId, mediaServerId));
    }

    void batchDel(List<StreamProxyItem> streamProxyItemList);

    void batchDelForGbStream(List<GbStream> gbStreams);

    void batchAdd(@Param("subList") List<StreamPushItem> subList);

    int updateStreamGPS(List<GPSMsgInfo> gpsMsgInfos);

    List<GbStream> selectAllForAppAndStream(List<StreamPushItem> streamPushItems);

    default void updateMediaServer(String app, String stream, String mediaServerId) {
        this.update(null, Wrappers.<GbStream>lambdaUpdate()
                .set(GbStream::getMediaServerId, mediaServerId)
                .eq(GbStream::getApp, app)
                .eq(GbStream::getStream, stream));
    }

    int updateGbIdOrName(List<StreamPushItem> streamPushItemForUpdate);

    Boolean selectStatusForProxy(@Param("app") String app, @Param("stream") String stream);

    Boolean selectStatusForPush(@Param("app") String app, @Param("stream") String stream);

}
