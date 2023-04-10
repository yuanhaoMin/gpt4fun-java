package com.rua.util;

import com.rua.model.request.Message;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SharedGPT35Logic {

    // TODO: Unit Test, think a better solution
    public long limitPromptTokensByPurgingHistoryMessages(long currentPromptTokens, long maxPromptTokens,
                                                          List<Message> historyMessages) {
        final var totalHistoryMessageLength = sumHistoryMessagesLengths(historyMessages);
        final var characterToPromptTokenConversionRatio = (double) currentPromptTokens / totalHistoryMessageLength;
        while (currentPromptTokens >= maxPromptTokens) {
            // Remove the earliest user message in history
            historyMessages.stream() //
                    .filter(message -> message.role().equals("user")).findFirst().ifPresent(historyMessages::remove);
            // Remove the earliest assistant message in history
            historyMessages.stream() //
                    .filter(message -> message.role().equals("assistant")).findFirst()
                    .ifPresent(historyMessages::remove);
            // Estimate current prompt tokens
            currentPromptTokens = (long) (sumHistoryMessagesLengths(
                    historyMessages) * characterToPromptTokenConversionRatio);
        }
        return currentPromptTokens;
    }

    public int sumHistoryMessagesLengths(final List<Message> historyMessages) {
        return historyMessages.stream() //
                .mapToInt(message -> message.content().length()) //
                .sum();
    }

    // TODO: Unit Test
    public void updateSystemMessage(@Nonnull final List<Message> historyMessages, final String systemMessageContent) {
        // Get the last system message, or an empty message if there are none
        final var lastSystemMessage = historyMessages.stream() //
                .filter(m -> m.role().equals("system")) //
                .findFirst() //
                .orElse(new Message("", ""));
        // Check if the new system message is different from the last one
        if (!systemMessageContent.equals(lastSystemMessage.content())) {
            historyMessages.remove(lastSystemMessage);
            historyMessages.add(0, new Message("system", systemMessageContent));
        }
    }

}