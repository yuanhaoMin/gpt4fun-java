package com.rua.util;

import com.rua.model.request.OpenAIChatCompletionMessage;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rua.constant.OpenAIConstants.*;

@Component
public class OpenAIChatCompletionLogic {

    // TODO: Unit Test, think a better solution
    public long limitPromptTokensByPurgingHistoryMessages(long currentPromptTokens, long maxPromptTokens,
                                                          @Nonnull final List<OpenAIChatCompletionMessage> historyMessages) {
        final var totalHistoryMessageLength = sumHistoryMessagesLengths(historyMessages);
        final var characterToPromptTokenConversionRatio = (double) currentPromptTokens / totalHistoryMessageLength;
        while (currentPromptTokens >= maxPromptTokens) {
            // Remove the earliest user message in history
            historyMessages.stream() //
                    .filter(message -> message.role().equals(CHAT_COMPLETION_ROLE_USER)).findFirst()
                    .ifPresent(historyMessages::remove);
            // Remove the earliest assistant message in history
            historyMessages.stream() //
                    .filter(message -> message.role().equals(CHAT_COMPLETION_ROLE_ASSISTANT)).findFirst()
                    .ifPresent(historyMessages::remove);
            // Estimate current prompt tokens
            currentPromptTokens = (long) (sumHistoryMessagesLengths(
                    historyMessages) * characterToPromptTokenConversionRatio);
        }
        return currentPromptTokens;
    }

    public int sumHistoryMessagesLengths(@Nonnull final List<OpenAIChatCompletionMessage> historyMessages) {
        return historyMessages.stream() //
                .mapToInt(message -> message.content().length()) //
                .sum();
    }

    public void updateSystemMessage(@Nonnull final List<OpenAIChatCompletionMessage> historyMessages,
                                    @Nonnull final String systemMessageContent) {
        final var newSystemMessage = new OpenAIChatCompletionMessage(CHAT_COMPLETION_ROLE_SYSTEM, systemMessageContent);
        int lastIndex = -1;
        // Search in reverse order since system message is usually the last one
        for (int i = historyMessages.size() - 1; i >= 0; i--) {
            OpenAIChatCompletionMessage message = historyMessages.get(i);
            if (message.role().equals(CHAT_COMPLETION_ROLE_SYSTEM)) {
                lastIndex = i;
                break;
            }
        }
        if (lastIndex >= 0) {
            historyMessages.remove(lastIndex);
        }
        // If lastSystemMessage is null, it means there is no system message in history, so we add a new one
        historyMessages.add(newSystemMessage);
    }

    public void shiftSystemMessageToHistoryEnd(@Nonnull final List<OpenAIChatCompletionMessage> historyMessages) {
        // For efficiency reason no stream is used here
        OpenAIChatCompletionMessage lastSystemMessage = null;
        int lastIndex = -1;
        for (int i = 0; i < historyMessages.size(); i++) {
            final var message = historyMessages.get(i);
            if (message.role().equals(CHAT_COMPLETION_ROLE_SYSTEM)) {
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