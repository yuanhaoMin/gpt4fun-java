package com.rua.service;

import com.rua.entity.ChamberUserCompletion;
import com.rua.logic.ChamberCompletionLogic;
import com.rua.model.request.ChamberUpdateCompletionRequestBo;
import com.rua.model.request.OpenAICompletionRequestDto;
import com.rua.model.response.ChamberCompletionWithStreamResponseDto;
import com.rua.model.response.OpenAICompletionWithStreamResponseDto;
import com.rua.property.OpenAIProperties;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_CHAMBER;
import static com.rua.constant.OpenAIConstants.END_OF_STREAM;
import static com.rua.util.SharedFormatUtils.createLogMessage;
import static com.rua.util.SharedJsonUtils.parseJsonToObject;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChamberCompletionService {

    private final OpenAIClientService openAIClientService;

    private final OpenAIProperties openAIProperties;

    private final ChamberCompletionLogic chamberCompletionLogic;

    public Flux<ChamberCompletionWithStreamResponseDto> completionWithStream(final String username) {
        final var userCompletion = chamberCompletionLogic.findUserCompletionByUsername(username);
        final var apiKey = userCompletion.getUser().getApiKey();
        final var openAICompletionRequest = buildOpenAICompletionRequest(userCompletion);
        chamberCompletionLogic.validateUserCompletion(username, userCompletion, false);
        final var startTimestamp = System.currentTimeMillis();
        return openAIClientService.completionWithStream(apiKey, openAICompletionRequest) //
                .map(this::extractAndCollectResponseMessage) //
                .filter(response -> response.content() != null) //
                .doOnComplete(() -> {
                    final var logMessage = createLogMessage("completionWithStream", startTimestamp, username, userCompletion.getModel());
                    log.info(LOG_PREFIX_CHAMBER + logMessage);
                });
    }

    public String updateUserCompletion(final ChamberUpdateCompletionRequestBo request) {
        chamberCompletionLogic.updateUserCompletionByUsername(request);
        return "Success";
    }


    private OpenAICompletionRequestDto buildOpenAICompletionRequest(ChamberUserCompletion userCompletion) {
        return OpenAICompletionRequestDto.builder() //
                .model(userCompletion.getModel()) //
                .prompt(userCompletion.getMessage()) //
                .maxTokens(openAIProperties.maxTokensCompletion()) //
                .useStream(true) //
                .temperature(userCompletion.getTemperature()) //
                .build();
    }

    @Nonnull
    private ChamberCompletionWithStreamResponseDto extractAndCollectResponseMessage(final String openAIResponse) {
        if (openAIResponse.equals(END_OF_STREAM)) {
            return ChamberCompletionWithStreamResponseDto.builder() //
                    .content(END_OF_STREAM) //
                    .hasEnd(true) //
                    .build();
        }
        final var openAICompletionWithStreamResponseDto = parseJsonToObject(openAIResponse, OpenAICompletionWithStreamResponseDto.class);
        final var messageContent = openAICompletionWithStreamResponseDto != null ?
                openAICompletionWithStreamResponseDto.choices().get(0).text() :
                "";
        return ChamberCompletionWithStreamResponseDto.builder() //
                .content(messageContent) //
                .hasEnd(false) //
                .build();
    }

}