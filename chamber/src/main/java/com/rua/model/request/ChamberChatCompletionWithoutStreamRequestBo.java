package com.rua.model.request;

import com.rua.constant.OpenAIChatCompletionModelEnum;
import lombok.Builder;

@Builder
public record ChamberChatCompletionWithoutStreamRequestBo(OpenAIChatCompletionModelEnum model, //
                                                          String userMessage, //
                                                          double temperature, //
                                                          String username) {
}