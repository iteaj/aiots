package com.iteaj.iboot.plugin.ai.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.ai.memory.db.DBMemoryProvider;
import com.iteaj.iboot.plugin.ai.tools.WeatherTool;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.AiServices;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;

/**
 * 聊天接口
 */
@RestController
@RequestMapping("/ai/chat")
public class ChatController extends BaseController {

    /**
     * 普通聊天
     * @param message 用户提示词
     * @param sessionId 会话标识
     * @return ai响应消息
     */
    @GetMapping
    public Result<String> chat(String message, String sessionId) {
        QwenChatModel model = QwenChatModel.builder()
                .apiKey("sk-f6e0510b47514148b89c57bfc75bd329")
                .modelName("qwen-turbo")
                .build();

        final String chat = AiServices.builder(TestChat.class)
                .chatLanguageModel(model)
                .tools(new WeatherTool())
                .build().chat(message);

        return success(chat, chat);
    }

    /**
     * 流式模型聊天
     * @param message 用户提示词
     * @param sessionId 会话标识
     * @return ai响应消息
     */
    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Result<String>>> stream(String message, String sessionId) {
        Flux<Result<String>> flux = Flux.create(sink -> {
            try {
                QwenStreamingChatModel model = QwenStreamingChatModel.builder()
                        .apiKey("sk-f6e0510b47514148b89c57bfc75bd329")
                        .modelName("qwen-turbo")
                        .build();
//                model.chat();
                AiServices.builder(TestChat.class)
                        .tools(new WeatherTool())
                        .chatMemoryProvider(new DBMemoryProvider())
                        .streamingChatLanguageModel(model)
                        .build()
                        .stream(message)
                        .onError(sink::error)
                        .onCompleteResponse(chatResponse -> sink.complete())
                        .onPartialResponse(response -> sink.next(success(response, response)))
                        .start();
            } catch (Exception e) {
                sink.error(e);
            }
        });

//        model.
        return flux.timeout(Duration.ofSeconds(30), Mono.error(new ServiceException("超时")))
                .onErrorResume(throwable -> Mono.just(fail(throwable.getMessage())))
                .map(item -> ServerSentEvent.<Result<String>>builder().data(item).build())
                .doFinally(signalType -> {
                    if(signalType == SignalType.CANCEL) {
                        System.out.println("cancel");
                    } else if(signalType == SignalType.ON_COMPLETE) {
                        System.out.println("onComplete");
                    } else if(signalType == SignalType.ON_ERROR) {
                        System.out.println("onError");
                    }
                });
    }
}
