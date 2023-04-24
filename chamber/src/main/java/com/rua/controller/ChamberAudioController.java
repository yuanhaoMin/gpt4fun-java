package com.rua.controller;

import com.rua.constant.ChamberControllerConstants;
import com.rua.model.request.ChamberTranscriptionRequestDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import com.rua.service.ChamberAudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = ChamberControllerConstants.CHAMBER_AUDIO_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberAudioController {

    private final ChamberAudioService chamberAudioService;

    @PostMapping(value = "/transcription", //
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, //
            produces = MediaType.APPLICATION_JSON_VALUE)
    public OpenAITranscriptionResponseDto transcription(
            @ModelAttribute final ChamberTranscriptionRequestDto request) {
        return chamberAudioService.transcription(request);
    }

}