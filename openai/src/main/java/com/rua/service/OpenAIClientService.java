package com.rua.service;

import com.rua.OpenAIClient;
import com.rua.model.request.OpenAIGPT35ChatRequestDto;
import com.rua.model.request.OpenAISpeechToTextRequestDto;
import com.rua.model.response.OpenAIGPT35ChatWithStreamData;
import com.rua.model.response.OpenAIGPT35ChatWithoutStreamResponseDto;
import com.rua.model.response.OpenAIWhisperTranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.rua.constant.OpenAIConstants.LOG_PREFIX_OPENAI;
import static com.rua.util.SharedDataUtils.parseJsonToObject;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIClient openAIClient;

    public List<OpenAIGPT35ChatWithStreamData> gpt35ChatWithStream(final OpenAIGPT35ChatRequestDto request) {
        if (!request.hasStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = true");
        }
        final var plainText = openAIClient.chatWithStream(request);
        return extractChatData(plainText);
    }

    public OpenAIGPT35ChatWithoutStreamResponseDto gpt35ChatWithoutStream(final OpenAIGPT35ChatRequestDto request) {
        if (request.hasStream()) {
            throw new IllegalArgumentException(LOG_PREFIX_OPENAI + "Request must have stream = false");
        }
        return openAIClient.chatWithoutStream(request);
    }

    public OpenAIWhisperTranscriptionResponseDto whisperCreateTranscription(
            final OpenAISpeechToTextRequestDto request) {
        final var model = request.model();
        final var audioFile = request.file();
        return openAIClient.createTranscription(model, audioFile);
    }

    private List<OpenAIGPT35ChatWithStreamData> extractChatData(final String input) {
        final List<OpenAIGPT35ChatWithStreamData> dataList = new ArrayList<>();
        final var patternString = "data: (\\{.*?\"choices\":\\[\\{.*?}]})";
        final var pattern = Pattern.compile(patternString);
        final var matcher = pattern.matcher(input);
        while (matcher.find()) {
            final var dataString = matcher.group(1);
            if (dataString.contains("{\"delta\":{\"content\"")) {
                final var data = parseJsonToObject(dataString, OpenAIGPT35ChatWithStreamData.class);
                dataList.add(data);
            }
        }
        return dataList;
    }

}