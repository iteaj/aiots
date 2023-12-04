package com.genersoft.iot.vmp.storager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

    @Select("<script> "+
            "SELECT gs.* FROM wvp_gb_stream gs " +
            "WHERE " +
            "1=1 " +
            " <if test='catalogId != null'> AND gs.gb_stream_id in" +
            "(select pgs.gb_stream_id from wvp_platform_gb_stream pgs where pgs.platform_id = #{platformId} and pgs.catalog_id=#{catalogId})</if> " +
            " <if test='catalogId == null'> AND gs.gb_stream_id not in" +
            "(select pgs.gb_stream_id from wvp_platform_gb_stream pgs where pgs.platform_id = #{platformId}) </if> " +
            " <if test='query != null'> AND (gs.app LIKE concat('%',#{query},'%') OR gs.stream LIKE concat('%',#{query},'%') OR gs.gb_id LIKE concat('%',#{query},'%') OR gs.name LIKE concat('%',#{query},'%'))</if> " +
            " <if test='mediaServerId != null' > AND gs.media_server_id=#{mediaServerId} </if>" +
            " order by gs.gb_stream_id asc " +
            "</script>")
    List<GbStream> selectAll(@Param("platformId") String platformId, @Param("catalogId") String catalogId
            , @Param("query") String query, @Param("mediaServerId") String mediaServerId);

    default GbStream selectOne(@Param("app") String app, @Param("stream") String stream) {
        return this.selectOne(Wrappers.<GbStream>lambdaQuery().eq(GbStream::getApp, app).eq(GbStream::getStream, stream));
    }

    default List<GbStream> selectByGBId(String gbId) {
        return this.selectList(Wrappers.<GbStream>lambdaQuery().eq(GbStream::getGbId, gbId));
    }

    GbStream queryStreamInPlatform(@Param("platformId") String platformId, @Param("gbId") String gbId);

    List<DeviceChannel> queryGbStreamListInPlatform(String platformId, @Param("usPushingAsStatus") boolean usPushingAsStatus);


    @Select("SELECT gs.* FROM wvp_gb_stream gs left join wvp_platform_gb_stream pgs " +
            "ON gs.gb_stream_id = pgs.gb_stream_id WHERE pgs.gb_stream_id is NULL")
    List<GbStream> queryStreamNotInPlatform();

    @Delete("DELETE FROM wvp_gb_stream WHERE stream_type=#{type} AND gb_id=NULL AND media_server_id=#{mediaServerId}")
    void deleteWithoutGBId(@Param("type") String type, @Param("mediaServerId") String mediaServerId);

    @Delete("<script> "+
            "DELETE FROM wvp_gb_stream where " +
            "<foreach collection='streamProxyItemList' item='item' separator='or'>" +
            "(app=#{item.app} and stream=#{item.stream}) " +
            "</foreach>" +
            "</script>")
    void batchDel(List<StreamProxyItem> streamProxyItemList);

    @Delete("<script> "+
            "DELETE FROM wvp_gb_stream where " +
            "<foreach collection='gbStreams' item='item' separator='or'>" +
            "(app=#{item.app} and stream=#{item.stream}) " +
            "</foreach>" +
            "</script>")
    void batchDelForGbStream(List<GbStream> gbStreams);

    @Insert("<script> " +
            "INSERT into wvp_gb_stream " +
            "(app, stream, gb_id, name, " +
            "longitude, latitude, stream_type,media_server_id,create_time)" +
            "values " +
            "<foreach collection='subList' index='index' item='item' separator=','> " +
            "(#{item.app}, #{item.stream}, #{item.gbId}, #{item.name}, " +
            "#{item.longitude}, #{item.latitude}, #{item.streamType}, " +
            "#{item.mediaServerId}, #{item.createTime}) "+
            "</foreach> " +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "gbStreamId", keyColumn = "gb_stream_id")
    void batchAdd(@Param("subList") List<StreamPushItem> subList);

    @Update({"<script>" +
            "<foreach collection='gpsMsgInfos' item='item' separator=';'>" +
            " UPDATE" +
            " wvp_gb_stream" +
            " SET longitude=#{item.lng}, latitude=#{item.lat} " +
            "WHERE gb_id=#{item.id}"+
            "</foreach>" +
            "</script>"})
    int updateStreamGPS(List<GPSMsgInfo> gpsMsgInfos);

    @Select("<script> "+
                   "SELECT * FROM wvp_gb_stream where " +
                   "<foreach collection='streamPushItems' item='item' separator='or'>" +
                   "(app=#{item.app} and stream=#{item.stream}) " +
                   "</foreach>" +
                   "</script>")
    List<GbStream> selectAllForAppAndStream(List<StreamPushItem> streamPushItems);

    @Update("UPDATE wvp_gb_stream " +
            "SET media_server_id=#{mediaServerId}" +
            "WHERE app=#{app} AND stream=#{stream}")
    void updateMediaServer(String app, String stream, String mediaServerId);

    @Update("<script> "+
                " <foreach collection='list' item='item' index='index' separator=';'>"+
                    "UPDATE wvp_gb_stream " +
                    " SET name=#{item.name},"+
                    " gb_id=#{item.gb_id}"+
                    " WHERE app=#{item.app} and stream=#{item.stream}"+
                "</foreach>"+
            "</script>")
    int updateGbIdOrName(List<StreamPushItem> streamPushItemForUpdate);

    @Select("SELECT status FROM wvp_stream_proxy WHERE app=#{app} AND stream=#{stream}")
    Boolean selectStatusForProxy(@Param("app") String app, @Param("stream") String stream);

    @Select("SELECT status FROM wvp_stream_push WHERE app=#{app} AND stream=#{stream}")
    Boolean selectStatusForPush(@Param("app") String app, @Param("stream") String stream);

}
