package com.rua.service;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.rua.model.GPTCompleteChatRequest;
import com.rua.util.SharedFormatUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.rua.constant.GPTConstants.LOG_PREFIX_GPT;

@RequiredArgsConstructor
@Service
public class GPTChatService {

    private static final Logger logger = LoggerFactory.getLogger(GPTChatService.class);

    private final ChatGPT chatGPTClient;

    public ChatCompletionResponse gpt35CompleteChat(final GPTCompleteChatRequest request) {
        final var startTime = System.currentTimeMillis();
        final var chatCompletion = ChatCompletion.builder() //
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName()) //
                .messages(request.messages()) //
                .maxTokens(request.maxCompletionTokens()) //
                .temperature(0.9) //
                .build();
        final var response = chatGPTClient.chatCompletion(chatCompletion);
        final var endTime = System.currentTimeMillis();
        final var executionTime = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(endTime - startTime);
        logger.info(LOG_PREFIX_GPT + "Chat completed in {}s", executionTime);
        return response;
    }

}