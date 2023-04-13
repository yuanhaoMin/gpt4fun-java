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
        // Get the last system message, or an empty message if there are none
        final var lastSystemMessage = historyMessages.stream() //
                .filter(m -> m.role().equals(GPT35TURBO_SYSTEM)) //
                .findFirst() //
                .orElse(new OpenAIGPT35ChatMessage("", ""));
        // Check if the new system message is different from the last one
        if (!systemMessageContent.equals(lastSystemMessage.content())) {
            historyMessages.remove(lastSystemMessage);
            historyMessages.add(0, new OpenAIGPT35ChatMessage(GPT35TURBO_SYSTEM, systemMessageContent));
        }
    }

}