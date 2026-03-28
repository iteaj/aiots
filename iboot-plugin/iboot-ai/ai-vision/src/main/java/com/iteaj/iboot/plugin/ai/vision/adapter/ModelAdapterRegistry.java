package com.iteaj.iboot.plugin.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.adapter.ModelAdapter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 模型适配器注册中心
 * <p>负责管理所有 {@link ModelAdapter} 实现，按模型名称/路径自动路由到匹配的适配器。</p>
 *
 * <p><b>路由规则：</b></p>
 * <ol>
 *   <li>遍历所有注入的 ModelAdapter，按 Spring {@code @Order} 优先级排序（值越小优先级越高）</li>
 *   <li>调用 {@link ModelAdapter#supports(String)} 找到第一个匹配的适配器</li>
 *   <li>同时校验适配器类型与期望的子接口类型一致</li>
 * </ol>
 *
 * <p><b>扩展方式：</b>实现对应的子接口（如 {@link com.iteaj.framework.spi.ai.vision.adapter.DetectionAdapter}），
 * 加上 {@code @Component} 和 {@code @Order} 注解即可自动注册，无需修改任何现有代码。</p>
 */
@Component
public class ModelAdapterRegistry {

    /** 按 @Order 排序后的所有适配器列表 */
    private final List<ModelAdapter<?>> adapters;

    public ModelAdapterRegistry(List<ModelAdapter<?>> adapters) {
        // 按 @Order / Ordered 接口排序，支持优先级控制；数值越小优先级越高
        this.adapters = adapters.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .collect(Collectors.toList());
    }

    /**
     * 按模型名/路径和适配器子接口类型匹配适配器
     *
     * @param modelNameOrPath 模型名称（如 {@code yolov8n}）或完整路径（如 {@code /models/yolov8n.onnx}）
     * @param adapterType     期望的适配器子接口类型，如 {@code DetectionAdapter.class}
     * @param <A>             适配器子接口类型
     * @return 匹配到的适配器实例
     * @throws IllegalArgumentException 未找到匹配的适配器时抛出，提示用户实现对应接口
     */
    @SuppressWarnings("unchecked")
    public <A extends ModelAdapter<?>> A match(String modelNameOrPath, Class<A> adapterType) {
        return (A) adapters.stream()
                .filter(a -> adapterType.isInstance(a) && a.supports(modelNameOrPath))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "未找到匹配模型 [" + modelNameOrPath + "] 的 "
                        + adapterType.getSimpleName() + " 适配器。\n"
                        + "请实现 " + adapterType.getSimpleName() + " 接口并用 @Component 注入 Spring，"
                        + "同时在 supports() 方法中声明支持该模型名称。"));
    }
}
