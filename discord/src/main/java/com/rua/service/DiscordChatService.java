package com.rua.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.rua.constant.DiscordConstants;
import com.rua.entity.DiscordGuildChatLog;
import com.rua.model.DiscordCompleteChatRequestBo;
import com.rua.repository.DiscordGuildChatLogRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@Service
public class DiscordChatService {

    private final ChatService chatService;

    private final DiscordGuildChatLogRepository discordGuildChatLogRepository;

    public String completeChat(final DiscordCompleteChatRequestBo request, final String userMessageContent) {
        final var gson = new Gson();
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(request.getGuildId());
        final List<Message> historyMessages = retrieveHistoryMessages(gson, guildChatLog);
        historyMessages.add(Message.of(userMessageContent));
        request.setMessages(historyMessages);
        final var response = chatService.completeChat(request);
        return updateChatLogAndSendCreateResponse(gson, guildChatLog, historyMessages, request, response);
    }

    public void resetChatHistory(final String guildId) {
        final var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        if (guildChatLog != null) {
            guildChatLog.setMessages("");
            discordGuildChatLogRepository.save(guildChatLog);
        }
    }

    // TODO: Unit Test
    public void updateSystemMessage(final String guildId, final String systemMessageContent) {
        final var gson = new Gson();
        var guildChatLog = discordGuildChatLogRepository.findByGuildId(guildId);
        final var historyMessages = retrieveHistoryMessages(gson, guildChatLog);
        // Get the last system message, or an empty message if there are none
        final var lastSystemMessage = historyMessages.stream() //
                .filter(m -> m.getRole().equals(Message.Role.SYSTEM.getValue())) //
                .findFirst() //
                .orElse(Message.ofSystem(""));
        // Check if the new system message is different from the last one
        if (!systemMessageContent.equals(lastSystemMessage.getContent())) {
            historyMessages.remove(lastSystemMessage);
            historyMessages.add(0, Message.ofSystem(systemMessageContent));
        }
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
            guildChatLog.setGuildId(guildId);
        }
        guildChatLog.setMessages(gson.toJson(historyMessages));
        discordGuildChatLogRepository.save(guildChatLog);
    }

    private List<Message> retrieveHistoryMessages(final Gson gson, final DiscordGuildChatLog guildChatLog) {
        if (guildChatLog == null) {
            return new ArrayList<>();
        }
        List<Message> historyMessages = gson.fromJson(guildChatLog.getMessages(), new TypeToken<ArrayList<Message>>() {
        }.getType());
        return historyMessages != null ? historyMessages : new ArrayList<>();
    }

    private String updateChatLogAndSendCreateResponse(final Gson gson, DiscordGuildChatLog guildChatLog,
                                                      final List<Message> historyMessages,
                                                      final DiscordCompleteChatRequestBo request,
                                                      final ChatCompletionResponse response) {
        final var botResponseContent = new StringBuilder();
        // Update chat history
        final var gptResponseContent = response.getChoices().get(0).getMessage().getContent();
        historyMessages.add(new Message(Message.Role.ASSISTANT.getValue(), gptResponseContent));
        // The conversion between Chinese characters and Token is greater than 1, subtract 3 when comparing
        if (response.getUsage().getCompletionTokens() >= request.getMaxCompletionTokens() - 3) {
            botResponseContent.append(
                            String.format(DiscordConstants.EXCEED_MAX_RESPONSE_TOKENS, request.getMaxCompletionTokens())) //
                    .append('\n');
        }
        // Current total tokens + next time user message tokens = next time prompt tokens
        purgeHistoryMessages(response.getUsage().getTotalTokens(), historyMessages, request.getMaxPromptTokens(),
                botResponseContent);
        botResponseContent.append("ChatGPT answers ").append(request.getUserName()).append(":\n");
        // Save guild chat log
        if (guildChatLog == null) {
            guildChatLog = new DiscordGuildChatLog();
        }
        guildChatLog.setGuildId(request.getGuildId());
        guildChatLog.setMessages(gson.toJson(historyMessages));
        guildChatLog.setLastChatTime(request.getLastChatTime());
        guildChatLog.setLastChatUserName(request.getUserName());
        discordGuildChatLogRepository.save(guildChatLog);
        botResponseContent.append(gptResponseContent);
        return botResponseContent.toString();
    }

    private void purgeHistoryMessages(long currentPromptTokens, List<Message> historyMessages, int maxPromptTokens,
                                      StringBuilder botResponseContent) {
        final var initialPromptCharacters = sumHistoryMessagesLengths(historyMessages);
        final var initialPromptTokens = currentPromptTokens;
        while (currentPromptTokens >= maxPromptTokens) {
            // Remove the earliest user message in history
            historyMessages.stream() //
                    .filter(message -> message.getRole().equals(Message.Role.USER.getValue())).findFirst().ifPresent(
                            historyMessages::remove);
            // Remove the earliest assistant message in history
            historyMessages.stream() //
                    .filter(message -> message.getRole().equals(
                            Message.Role.ASSISTANT.getValue())).findFirst().ifPresent(historyMessages::remove);
            // Estimate current prompt tokens
            final var conversionRatio = (double) initialPromptTokens / initialPromptCharacters;
            currentPromptTokens = (long) (sumHistoryMessagesLengths(historyMessages) * conversionRatio);
            if (currentPromptTokens < maxPromptTokens) {
                botResponseContent.append(String.format(DiscordConstants.EXCEED_MAX_PROMPT_TOKENS, //
                                initialPromptTokens, //
                                maxPromptTokens, //
                                currentPromptTokens, //
                                maxPromptTokens)) //
                        .append('\n');
            }
        }
    }

    private int sumHistoryMessagesLengths(final List<Message> historyMessages) {
        return historyMessages.stream() //
                .mapToInt(message -> message.getContent().length()) //
                .sum();
    }

}