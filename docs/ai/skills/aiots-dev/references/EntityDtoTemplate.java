package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Date;
import java.util.List;

/**
 * xxx记录 DTO数据模型
 * 
 * 【说明】
 * 1. DTO用于扩展实体类，包含搜索条件、关联数据等额外字段
 * 2. 继承实体类，自动拥有实体类的所有字段
 * 
 * 【表关联处理】
 * - 一对一关联：添加关联的实体类字段（如：private User user;）
 * - 一对多关联：添加关联的实体列表字段（如：private List<Device> devices;）
 * - XML实现详见：[MapperTemplate.xml](./MapperTemplate.xml)
 * 
 * @author iteaj
 * @since ${date}
 */
@Getter
@Setter
@Accessors(chain = true)
public class XxxDto extends Xxx {


// ===== 额外的搜索条件 =====
    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;


// ===== 一对一关联实体示例 =====
    /**
     * 关联的用户信息(一对一)
     * 在Service层的detailById()方法中填充此字段
     */
    // private Device device;          // 关联的设备


// ===== 一对多关联列表示例 =====
    /**
     * 关联的子记录列表(一对多)
     * 在Service层的detailById()方法中填充此字段
     */
    // private List<Device> devices;          // 设备列表


// ===== 完整示例：设备DTO =====
    /*
    public class DeviceDto extends Device {
        // 搜索条件
        private Date startTime;           // 开始时间
        private Date endTime;             // 结束时间
        private String deviceNameLike;    // 设备名称模糊查询
        
        // 一对一关联
        private Product product;          // 关联的产品信息
        
        // 一对多关联
        private List<DeviceAttr> attrs;   // 设备属性列表
    }
    */
}