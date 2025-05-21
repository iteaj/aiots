package com.iteaj.iboot.plugin.ai.memory.db;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;

public class DBMemoryProvider implements ChatMemoryProvider {

    @Override
    public ChatMemory get(Object memoryId) {
        return new DBChatMemory();
    }
}
