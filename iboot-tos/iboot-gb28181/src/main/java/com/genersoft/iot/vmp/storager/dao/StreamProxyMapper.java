package com.genersoft.iot.vmp.storager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genersoft.iot.vmp.media.zlm.dto.StreamProxyItem;
import com.genersoft.iot.vmp.vmanager.bean.ResourceBaseInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StreamProxyMapper extends BaseMapper<StreamProxyItem> {

    Page<StreamProxyItem> listDetailByPage(Page page, StreamProxyItem entity);

    List<StreamProxyItem> selectForEnable(boolean enable);

    StreamProxyItem selectOne(@Param("app") String app, @Param("stream") String stream);

    List<StreamProxyItem> selectForEnableInMediaServer( @Param("id")  String id, @Param("enable") boolean enable);

    @Select("SELECT st.*, pgs.gb_id, pgs.name, pgs.longitude, pgs.latitude FROM wvp_stream_proxy st " +
            "LEFT join wvp_gb_stream pgs on st.app = pgs.app AND st.stream = pgs.stream " +
            "WHERE st.media_server_id= #{id} order by st.create_time desc")
    List<StreamProxyItem> selectInMediaServer(String id);

    @Update("UPDATE wvp_stream_proxy " +
            "SET status=#{status} " +
            "WHERE media_server_id=#{mediaServerId}")
    void updateStatusByMediaServerId(@Param("mediaServerId") String mediaServerId, @Param("status") boolean status);

    @Update("UPDATE wvp_stream_proxy " +
            "SET status=#{status} " +
            "WHERE app=#{app} AND stream=#{stream}")
    int updateStatus(@Param("app") String app, @Param("stream") String stream, @Param("status") boolean status);

    @Delete("DELETE FROM wvp_stream_proxy WHERE enable_remove_none_reader=true AND media_server_id=#{mediaServerId}")
    void deleteAutoRemoveItemByMediaServerId(String mediaServerId);

    @Select("SELECT st.*, pgs.gb_id, pgs.name, pgs.longitude, pgs.latitude FROM wvp_stream_proxy st LEFT join wvp_gb_stream pgs on st.app = pgs.app AND st.stream = pgs.stream WHERE st.enable_remove_none_reader=true AND st.media_server_id=#{mediaServerId} order by st.create_time desc")
    List<StreamProxyItem> selectAutoRemoveItemByMediaServerId(String mediaServerId);

    @Select("select count(1) as total, sum(status) as online from wvp_stream_proxy")
    ResourceBaseInfo getOverview();

    @Select("select count(1) from wvp_stream_proxy")

    int getAllCount();

    @Select("select count(1) from wvp_stream_proxy where status = true")
    int getOnline();
}
