package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.Dataset;
import com.iteaj.iboot.module.iot.entity.Le5LeDatasource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Le5LeDatasourceMapper extends BaseMapper<Le5LeDatasource> {

    Page detailPage(Page page, Le5LeDatasource entity);

    List<Dataset> listPointDatasetByDeviceGroupId(Long deviceGroupId);

    List<Dataset> listWarnDatasetByDeviceGroupId(Long deviceGroupId);

    List<Dataset> listStatusDatasetByDeviceGroupId(Long deviceGroupId);
}
