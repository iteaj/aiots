package com.iteaj.iboot.plugin.le5le.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.le5le.entity.Dataset;
import com.iteaj.iboot.plugin.le5le.entity.Le5LeDatasource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Le5LeDatasourceMapper extends BaseMapper<Le5LeDatasource> {

    Page detailPage(Page page, Le5LeDatasource entity);

    List<Dataset> listPointDatasetByDeviceGroupId(Long deviceGroupId);

    List<Dataset> listWarnDatasetByDeviceGroupId(Long deviceGroupId);

    List<Dataset> listStatusDatasetByDeviceGroupId(Long deviceGroupId);

    Page<Le5LeDatasource> pageDateSet(Page page, Le5LeDatasource entity);
}
