package com.iteaj.iboot.plugin.gbs.rtvs;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.plugin.gbs.dto.GbsViewChannelDto;

import java.util.List;

public interface RtvsChannelMapper extends BaseMapper<RtvsChannel> {

    List<GbsViewChannelDto> listViewChannels();
}
