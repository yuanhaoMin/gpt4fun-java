package com.rua.util;


import com.rua.model.request.OpenAIGPT35ChatMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.OpenAIConstants.GPT35TURBO_SYSTEM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        assertEquals(1, history.size());
        assertEquals(GPT35TURBO_SYSTEM, history.get(0).role());
        assertEquals(systemMessageContent, history.get(0).content());
    }

    @Test
    void testUpdateSystemMessage_whenSameMessage_shouldNotUpdate() {
        // Given
        final List<OpenAIGPT35ChatMessage> history = new ArrayList<>();
        final var mockedMessage = mock(OpenAIGPT35ChatMessage.class);
        history.add(mockedMessage);
        final var systemMessageContent = "Same message";

        // When
        when(mockedMessage.role()).thenReturn(GPT35TURBO_SYSTEM);
        when(mockedMessage.content()).thenReturn(systemMessageContent);
        classUnderTest.updateSystemMessage(history, systemMessageContent);

        // Then
        verify(mockedMessage, times(1)).role();
        verify(mockedMessage, times(1)).content();

        assertEquals(1, history.size());
        assertEquals(mockedMessage, history.get(0));
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
        assertEquals(1, history.size());
        assertEquals(GPT35TURBO_SYSTEM, history.get(0).role());
        assertEquals(systemMessageContent, history.get(0).content());
    }

}