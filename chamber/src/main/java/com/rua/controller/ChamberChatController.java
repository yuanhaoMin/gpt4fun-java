package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.ChamberChatMessageRequest;
import com.rua.model.ChamberChatMessageResponse;
import com.rua.service.ChamberChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_CHAT_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberChatController {

    private final ChamberChatService chamberChatService;

    // TODO validation
    @PostMapping(value = "/messages")
    public ChamberChatMessageResponse sendMessageAndGetResponse(@RequestBody ChamberChatMessageRequest request) {
        final var responseMessage = chamberChatService.gpt35completeChat(request);
        return ChamberChatMessageResponse.builder() //
                .responseMessage(responseMessage) //
                .build();
    }

    @DeleteMapping(value = "/history")
    public String deleteChatHistory(long userId) {
        return "Chat history deleted";
    }

}