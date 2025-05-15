package com.iteaj.iboot.plugin.ai.controller;

import com.alibaba.dashscope.common.DashScopeResult;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import dev.langchain4j.community.model.dashscope.QwenLanguageModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;

/**
 * 聊天管理
 */
@RestController
@RequestMapping("/ai/chat")
public class ChatController extends BaseController {

    @GetMapping(value = "bot", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Result<String>>> chat(String message) {
        Flux<Result<String>> flux = Flux.create(sink -> {
            try {
                QwenStreamingChatModel model = QwenStreamingChatModel.builder()
                        .apiKey("sk-f6e0510b47514148b89c57bfc75bd329")
                        .modelName("qwen-turbo")
                        .build();
            model.chat(message, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    sink.next(Result.success(partialResponse));
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });
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
