package com.rua.model.response;

import com.rua.model.request.Message;

public record Choice(Integer index, Message message, String finishReason) {
}