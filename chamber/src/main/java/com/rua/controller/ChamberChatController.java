package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.ChamberChatMessageRequest;
import com.rua.model.response.OpenAIGPT35ChatResponse;
import com.rua.service.ChamberChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_CHAT_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberChatController {

    private final ChamberChatService chamberChatService;

    // TODO validation
    // TODO Exceeding limit error handling instead of 500
    @PostMapping(value = "/messages")
    public OpenAIGPT35ChatResponse sendMessageAndGetResponse(@RequestBody final ChamberChatMessageRequest request) {
        return chamberChatService.gpt35completeChat(request);
    }

    @DeleteMapping(value = "/history")
    public String deleteChatHistory(final long userId) {
        return "Chat history deleted";
    }

}