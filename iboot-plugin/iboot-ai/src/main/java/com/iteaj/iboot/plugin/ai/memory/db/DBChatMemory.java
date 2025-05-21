package com.iteaj.iboot.plugin.ai.memory.db;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;

import java.util.List;

public class DBChatMemory implements ChatMemory {

    @Override
    public Object id() {
        return null;
    }

    @Override
    public void add(ChatMessage message) {

    }

    @Override
    public List<ChatMessage> messages() {
        return List.of();
    }

    @Override
    public void clear() {

    }
}
