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

import static com.rua.util.SharedDataUtils.parseJsonToObject;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {

    private final OpenAIClient openAIClient;

    public List<OpenAIGPT35ChatWithStreamData> chatWithStream(final OpenAIGPT35ChatRequestDto requestDto) {
        final var plainText = openAIClient.chatWithStream(requestDto);
        return parseData(plainText);
    }

    public OpenAIGPT35ChatWithoutStreamResponseDto chatWithoutStream(final OpenAIGPT35ChatRequestDto requestDto) {
        return openAIClient.chatWithoutStream(requestDto);
    }

    public OpenAIWhisperTranscriptionResponseDto createTranscription(final OpenAISpeechToTextRequestDto request) {
        final var model = request.model();
        final var audioFile = request.file();
        return openAIClient.createTranscription(model, audioFile);
    }

    private List<OpenAIGPT35ChatWithStreamData> parseData(final String input) {
        final List<OpenAIGPT35ChatWithStreamData> dataList = new ArrayList<>();
        final var patternString = "data: (\\{.*?\"choices\":\\[\\{.*?\\}\\]\\})";
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