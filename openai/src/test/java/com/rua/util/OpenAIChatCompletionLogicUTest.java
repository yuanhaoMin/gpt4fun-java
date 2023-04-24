package com.rua.util;


import com.rua.model.request.OpenAIChatCompletionMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.OpenAIConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAIChatCompletionLogicUTest {

    private final OpenAIChatCompletionLogic classUnderTest = new OpenAIChatCompletionLogic();

    @Test
    void testUpdateSystemMessage_whenNoHistory_shouldCreate() {
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
    void testUpdateSystemMessage_whenHistoryButNoSystemMessage_shouldCreate() {
        // Given
        final List<OpenAIChatCompletionMessage> history = new ArrayList<>();
        history.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, "User message"));
        final var systemMessageContent = "System message";

        // When
        classUnderTest.updateSystemMessage(history, systemMessageContent);

        // Then
        final var lastMessage = history.get(history.size() - 1);
        assertEquals(2, history.size());
        assertEquals(CHAT_COMPLETION_ROLE_SYSTEM, lastMessage.role());
        assertEquals(systemMessageContent, lastMessage.content());
    }

    @Test
    void testUpdateSystemMessage_whenHistoryAndSystemMessage_shouldUpdate() {
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


    @Test
    void testShiftSystemMessageToHistoryEnd_whenNoSystemMessage_shouldNotShift() {
        // Given
        final List<OpenAIChatCompletionMessage> history = new ArrayList<>();
        history.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, "User message"));

        // When
        classUnderTest.shiftSystemMessageToHistoryEnd(history);

        // Then
        assertEquals(1, history.size());
    }

    @Test
    void testShiftSystemMessageToHistoryEnd_whenSystemMessage_shouldShift() {
        // Given
        final List<OpenAIChatCompletionMessage> history = new ArrayList<>();
        final var systemMessageContent = "System message";
        history.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_SYSTEM, systemMessageContent));
        history.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_USER, "User message"));
        history.add(new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_ASSISTANT, "Assistant message"));

        // When
        classUnderTest.shiftSystemMessageToHistoryEnd(history);

        // Then
        assertEquals(3, history.size());
        final var lastMessage = history.get(history.size() - 1);
        assertEquals(CHAT_COMPLETION_ROLE_SYSTEM, lastMessage.role());
        assertEquals(systemMessageContent, lastMessage.content());
    }

}