package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.request.OpenAISpeechToTextRequest;
import com.rua.model.response.OpenAIWhisperTranscriptionResponse;
import com.rua.service.ChamberSpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_SPEECH_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberSpeechController {

    private final ChamberSpeechService chamberSpeechService;

    // TODO validation
    @PostMapping(value = "/speech-to-text", //
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, //
            produces = MediaType.APPLICATION_JSON_VALUE)
    public OpenAIWhisperTranscriptionResponse convertVoiceToText(
            @ModelAttribute final OpenAISpeechToTextRequest request) {
        return chamberSpeechService.convertVoiceToText(request);
    }

}