
package com.iteaj.iboot.plugin.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI插件配置属性
 */
@ConfigurationProperties(prefix = "iboot.ai")
public class AiProperties {

    /**
     * AI服务API密钥
     */
    private String apiKey;

    /**
     * AI服务基础URL
     */
    private String baseUrl = "https://api.openai.com";

    /**
     * 默认模型
     */
    private String defaultModel = "gpt-3.5-turbo";

    /**
     * 请求超时时间（毫秒）
     */
    private long timeout = 30000;

    /**
     * 是否启用AI插件
     */
    private boolean enabled = true;

    /**
     * gRPC服务地址（Python端）
     */
    private String grpcHost = "127.0.0.1";

    /**
     * gRPC服务端口（Python端）
     */
    private int grpcPort = 50051;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGrpcHost() {
        return grpcHost;
    }

    public void setGrpcHost(String grpcHost) {
        this.grpcHost = grpcHost;
    }

    public int getGrpcPort() {
        return grpcPort;
    }

    public void setGrpcPort(int grpcPort) {
        this.grpcPort = grpcPort;
    }
}
