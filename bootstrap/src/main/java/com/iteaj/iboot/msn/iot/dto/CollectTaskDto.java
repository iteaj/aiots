package com.iteaj.iboot.msn.iot.dto;

import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.CollectTask;
import lombok.Data;

import java.util.List;

@Data
public class CollectTaskDto extends CollectTask {

    /**
     * 点位列表
     */
    private List<CollectDetail> details;

}
