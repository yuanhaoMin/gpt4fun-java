package com.rua.util;

import com.rua.model.request.OpenAIGPT35ChatMessage;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rua.constant.OpenAIConstants.*;

@Component
public class OpenAIGPT35Logic {

    // TODO: Unit Test, think a better solution
    public long limitPromptTokensByPurgingHistoryMessages(long currentPromptTokens, long maxPromptTokens,
                                                          List<OpenAIGPT35ChatMessage> historyMessages) {
        final var totalHistoryMessageLength = sumHistoryMessagesLengths(historyMessages);
        final var characterToPromptTokenConversionRatio = (double) currentPromptTokens / totalHistoryMessageLength;
        while (currentPromptTokens >= maxPromptTokens) {
            // Remove the earliest user message in history
            historyMessages.stream() //
                    .filter(message -> message.role().equals(GPT35TURBO_USER)).findFirst()
                    .ifPresent(historyMessages::remove);
            // Remove the earliest assistant message in history
            historyMessages.stream() //
                    .filter(message -> message.role().equals(GPT35TURBO_ASSISTANT)).findFirst()
                    .ifPresent(historyMessages::remove);
            // Estimate current prompt tokens
            currentPromptTokens = (long) (sumHistoryMessagesLengths(
                    historyMessages) * characterToPromptTokenConversionRatio);
        }
        return currentPromptTokens;
    }

    public int sumHistoryMessagesLengths(final List<OpenAIGPT35ChatMessage> historyMessages) {
        return historyMessages.stream() //
                .mapToInt(message -> message.content().length()) //
                .sum();
    }

    public void updateSystemMessage(@Nonnull final List<OpenAIGPT35ChatMessage> historyMessages,
                                    @Nonnull final String systemMessageContent) {
        final var newSystemMessage = new OpenAIGPT35ChatMessage(GPT35TURBO_SYSTEM, systemMessageContent);
        if (historyMessages.isEmpty()) {
            historyMessages.add(newSystemMessage);
            return;
        }
        OpenAIGPT35ChatMessage lastSystemMessage = null;
        int lastIndex = -1;
        // We know system message is always the last one
        for (int i = historyMessages.size() - 1; i >= 0; i--) {
            OpenAIGPT35ChatMessage message = historyMessages.get(i);
            if (message.role().equals(GPT35TURBO_SYSTEM)) {
                lastSystemMessage = message;
                lastIndex = i;
                break;
            }
        }
        if (lastSystemMessage != null) {
            historyMessages.remove(lastIndex);
            historyMessages.add(newSystemMessage);
        }
    }

    public void shiftSystemMessageToHistoryEnd(@Nonnull final List<OpenAIGPT35ChatMessage> historyMessages) {
        // For efficiency reason no stream is used here
        OpenAIGPT35ChatMessage lastSystemMessage = null;
        int lastIndex = -1;
        for (int i = 0; i < historyMessages.size(); i++) {
            final var message = historyMessages.get(i);
            if (message.role().equals(GPT35TURBO_SYSTEM)) {
                lastSystemMessage = message;
                lastIndex = i;
                break;
            }
        }
        if (lastSystemMessage != null) {
            historyMessages.remove(lastIndex);
            historyMessages.add(lastSystemMessage);
        }
    }

}