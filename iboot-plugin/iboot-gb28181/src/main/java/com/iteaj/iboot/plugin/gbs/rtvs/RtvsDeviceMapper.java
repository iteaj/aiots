package com.iteaj.iboot.plugin.gbs.rtvs;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RtvsDeviceMapper extends BaseMapper<RtvsDevice> {

    Page<RtvsDevice> detailByPage(Page page, RtvsDevice entity);
}
