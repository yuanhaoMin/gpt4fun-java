package com.rua.service;

import com.rua.logic.ChamberChatLogic;
import com.rua.model.request.ChamberCompleteChatRequestBo;
import com.rua.model.request.OpenAIGPT35ChatMessage;
import com.rua.model.response.OpenAIGPT35ChatResponseDto;
import com.rua.property.ChamberProperties;
import com.rua.util.OpenAIGPT35Logic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rua.constant.ChamberConstants.*;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_ASSISTANT;
import static com.rua.constant.OpenAIConstants.GPT35TURBO_USER;

@RequiredArgsConstructor
@Service
public class ChamberChatService {

    private final OpenAIClientService openAIClientService;

    private final ChamberChatLogic chamberChatLogic;

    private final OpenAIGPT35Logic openAIGPT35Logic;

    private final ChamberProperties chamberProperties;

    public String gpt35completeChat(final ChamberCompleteChatRequestBo request) {
        var userChatLog = chamberChatLogic.findByUserId(request.userId());
        final List<OpenAIGPT35ChatMessage> messages = chamberChatLogic.retrieveHistoryMessages(userChatLog);
        // Add user message for this time prompt
        messages.add(new OpenAIGPT35ChatMessage(GPT35TURBO_USER, request.userMessage()));
        final var gptResponse = openAIClientService.chat(messages);
        // Add gpt response for next time prompt
        messages.add(
                new OpenAIGPT35ChatMessage(GPT35TURBO_ASSISTANT, gptResponse.choices().get(0).message().content()));
        final var processedResponse = generateResponseAndHandleTokenLimit(gptResponse, messages);
        chamberChatLogic.updateChamberUserChatLog(userChatLog, messages, request);
        return processedResponse;
    }

    public String resetChatHistory(final long userId) {
        chamberChatLogic.resetChatHistory(userId);
        return GPT_35_RESET_CHAT_HISTORY_SUCCESS;
    }

    public String updateSystemMessage(final long userId, final String systemMessageContent) {
        chamberChatLogic.updateSystemMessageAndPersist(userId, systemMessageContent);
        return String.format(GPT_35_SET_SYSTEM_MESSAGE_SUCCESS, systemMessageContent);
    }

    private String generateResponseAndHandleTokenLimit(final OpenAIGPT35ChatResponseDto gptResponse,
                                                       final List<OpenAIGPT35ChatMessage> historyMessages) {
        final var processedResponse = new StringBuilder();
        // Next time prompt tokens = current total tokens + estimated next time prompt tokens
        final var estimatedNextTimePromptTokens = gptResponse.usage()
                .totalTokens() + chamberProperties.estimatedPromptLength();
        if (estimatedNextTimePromptTokens == chamberProperties.maxTotalTokens()) {
            processedResponse.append(String.format(GPT_35_CHAT_TRUNCATE_RESPONSE, chamberProperties.maxPromptTokens()));
        }
        final var maxPromptTokens = chamberProperties.maxPromptTokens();
        // Need to purge history messages if next time prompt tokens exceed max prompt tokens
        if (estimatedNextTimePromptTokens >= maxPromptTokens) {
            processedResponse.append(String.format(GPT_35_CHAT_TOKEN_LIMIT, //
                    estimatedNextTimePromptTokens, //
                    chamberProperties.maxTotalTokens()));
            final var purgedPromptTokens = openAIGPT35Logic.limitPromptTokensByPurgingHistoryMessages(
                    estimatedNextTimePromptTokens, maxPromptTokens, historyMessages);
            processedResponse.append(String.format(GPT_35_CHAT_CLEAN_HISTORY, //
                    estimatedNextTimePromptTokens, //
                    maxPromptTokens, //
                    purgedPromptTokens, //
                    maxPromptTokens));
        }
        processedResponse.append(BOT_RESPONSE_PREFIX);
        processedResponse.append(gptResponse.choices().get(0).message().content());
        return processedResponse.toString();
    }

}