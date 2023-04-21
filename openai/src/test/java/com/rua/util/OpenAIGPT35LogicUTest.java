package com.rua.util;


import com.rua.model.request.OpenAIGPT35ChatMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.OpenAIConstants.GPT35TURBO_SYSTEM;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAIGPT35LogicUTest {

    private final OpenAIGPT35Logic classUnderTest = new OpenAIGPT35Logic();

    @Test
    void testUpdateSystemMessage_whenNoHistory_shouldUpdate() {
        // Given
        final List<OpenAIGPT35ChatMessage> history = new ArrayList<>();
        final var systemMessageContent = "System message";

        // When
        classUnderTest.updateSystemMessage(history, systemMessageContent);

        // Then
        final var lastMessage = history.get(history.size() - 1);
        assertEquals(1, history.size());
        assertEquals(GPT35TURBO_SYSTEM, lastMessage.role());
        assertEquals(systemMessageContent, lastMessage.content());
    }

    @Test
    void testUpdateSystemMessage_whenDifferentMessage_shouldUpdate() {
        // Given
        final List<OpenAIGPT35ChatMessage> history = new ArrayList<>();
        history.add(new OpenAIGPT35ChatMessage(GPT35TURBO_SYSTEM, "Old message"));
        final var systemMessageContent = "New message";

        // When
        classUnderTest.updateSystemMessage(history, systemMessageContent);

        // Then
        final var lastMessage = history.get(history.size() - 1);
        assertEquals(1, history.size());
        assertEquals(GPT35TURBO_SYSTEM, lastMessage.role());
        assertEquals(systemMessageContent, lastMessage.content());
    }

}