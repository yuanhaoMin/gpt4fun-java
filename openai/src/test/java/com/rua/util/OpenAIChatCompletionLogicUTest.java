package com.rua.util;


import com.rua.model.request.OpenAIChatCompletionMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.OpenAIConstants.CHAT_COMPLETION_ROLE_SYSTEM;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAIChatCompletionLogicUTest {

    private final OpenAIChatCompletionLogic classUnderTest = new OpenAIChatCompletionLogic();

    @Test
    void testUpdateSystemMessage_whenNoHistory_shouldUpdate() {
        // Given
        final List<OpenAIChatCompletionMessage> history = new ArrayList<>();
        final var systemMessageContent = "System message";

        // When
        classUnderTest.updateSystemMessage(history, systemMessageContent);

        // Then
        final var lastMessage = history.get(history.size() - 1);
        assertEquals(1, history.size());
        assertEquals(CHAT_COMPLETION_ROLE_SYSTEM, lastMessage.role());
        assertEquals(systemMessageContent, lastMessage.content());
    }

    @Test
    void testUpdateSystemMessage_whenDifferentMessage_shouldUpdate() {
        // Given
        final List<OpenAIChatCompletionMessage> history = new ArrayList<>();
        history.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_SYSTEM, "Old message"));
        final var systemMessageContent = "New message";

        // When
        classUnderTest.updateSystemMessage(history, systemMessageContent);

        // Then
        final var lastMessage = history.get(history.size() - 1);
        assertEquals(1, history.size());
        assertEquals(CHAT_COMPLETION_ROLE_SYSTEM, lastMessage.role());
        assertEquals(systemMessageContent, lastMessage.content());
    }

}