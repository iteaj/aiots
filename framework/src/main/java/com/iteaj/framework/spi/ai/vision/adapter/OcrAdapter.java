package com.iteaj.framework.spi.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.model.OcrResult;

import java.util.List;

/**
 * OCR 文字识别模型适配器接口
 * <p>适用于 PaddleOCR、TrOCR 等文字检测+识别模型，后处理返回文字区域列表</p>
 *
 * <p>内置实现：</p>
 * <ul>
 *   <li>{@code PaddleOcrAdapter} — 匹配 {@code paddleocr*}、{@code ch_pp*}</li>
 * </ul>
 */
public interface OcrAdapter extends ModelAdapter<List<OcrResult>> {
}
