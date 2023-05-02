package com.rua.controller;

import com.rua.model.request.ChamberTranscriptionRequestDto;
import com.rua.model.response.OpenAITranscriptionResponseDto;
import com.rua.service.ChamberAudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rua.constant.ChamberPathConstants.CHAMBER_AUDIO_CONTROLLER_PATH;
import static com.rua.constant.ChamberPathConstants.CHAMBER_AUDIO_TRANSCRIPTION_PATH;

@RequestMapping(value = CHAMBER_AUDIO_CONTROLLER_PATH)
@RequiredArgsConstructor
@RestController
public class ChamberAudioController {

    private final ChamberAudioService chamberAudioService;

    @PostMapping(value = CHAMBER_AUDIO_TRANSCRIPTION_PATH, //
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, //
            produces = MediaType.APPLICATION_JSON_VALUE)
    public OpenAITranscriptionResponseDto transcription(final Authentication authentication, //
                                                        @ModelAttribute final ChamberTranscriptionRequestDto request) {
        return chamberAudioService.transcription(request, authentication.getName());
    }

}