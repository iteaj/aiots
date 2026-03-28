package com.iteaj.framework.spi.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.model.ClassItem;

import java.util.List;

/**
 * 图像分类模型适配器接口
 * <p>适用于 ResNet、EfficientNet 等图像分类模型，后处理返回 Top-N 分类候选列表</p>
 *
 * <p>内置实现：</p>
 * <ul>
 *   <li>{@code ResNetClassificationAdapter} — 匹配 {@code resnet*}、{@code efficientnet*}</li>
 * </ul>
 */
public interface ClassificationAdapter extends ModelAdapter<List<ClassItem>> {
}
