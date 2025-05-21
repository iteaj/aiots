package com.iteaj.iboot.plugin.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class WeatherTool {

    @Tool(name = "获取当前天气")
    public String getWeather(@P(value = "城市", required = false) String city) {
        if(city == null) {
            return "没有指定城市";
        }

        return "北京温度33℃";
    }
}
