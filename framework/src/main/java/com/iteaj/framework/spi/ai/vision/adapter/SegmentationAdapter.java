package com.iteaj.framework.spi.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.model.SegmentItem;

import java.util.List;

/**
 * 图像分割模型适配器接口
 * <p>适用于 YOLOv8-seg 等实例分割模型，后处理返回分割区域列表（含掩码和轮廓）</p>
 *
 * <p>内置实现：</p>
 * <ul>
 *   <li>{@code YoloV8SegAdapter} — 匹配 {@code yolo*seg*}、{@code yolov8*seg*}</li>
 * </ul>
 */
public interface SegmentationAdapter extends ModelAdapter<List<SegmentItem>> {
}
