package com.rua.service;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.rua.model.CompleteChatRequestBo;
import com.rua.util.FormatUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatGPT chatGPTClient;

    public ChatCompletionResponse completeChat(final CompleteChatRequestBo request) {
        final long startTime = System.currentTimeMillis();
        final var chatCompletion = ChatCompletion.builder() //
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName()) //
                .messages(request.getMessages()) //
                .maxTokens(request.getMaxCompletionTokens()) //
                .temperature(0.9) //
                .build();
        final var response = chatGPTClient.chatCompletion(chatCompletion);
        final long endTime = System.currentTimeMillis();
        logger.info("GPT -- Chat completed in {}s",
                FormatUtils.convertMillisToStringWithMaxTwoFractionDigits(endTime - startTime));
        return response;
    }
}