package com.rua.model;

import com.plexpt.chatgpt.entity.chat.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
@SuperBuilder
public class CompleteChatRequestBo {

    private List<Message> messages;

    // max response tokens
    private final int maxCompletionTokens;

    // max prompt tokens including chat history
    private final int maxPromptTokens;

}