package com.iteaj.iboot.plugin.ai.controller;

import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface TestChat {

    @SystemMessage("按下面的条件回答: 如果查询的城市和获取的城市不一致需要提醒用户")
    String chat(String msg);

    TokenStream stream(String msg);
}
