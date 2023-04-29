package com.rua.service;

import com.rua.logic.ChamberCompletionLogic;
import com.rua.model.request.ChamberUpdateCompletionRequestBo;
import com.rua.model.request.OpenAICompletionRequestDto;
import com.rua.model.response.ChamberCompletionWithStreamResponseDto;
import com.rua.model.response.OpenAICompletionWithStreamResponseDto;
import com.rua.property.OpenAIProperties;
import com.rua.util.SharedFormatUtils;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.rua.constant.ChamberConstants.LOG_PREFIX_TIME_CHAMBER;
import static com.rua.constant.OpenAIConstants.END_OF_STREAM;
import static com.rua.util.SharedDataUtils.isNullOrEmpty;
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
        final var model = userCompletion.getModel();
        final var prompt = userCompletion.getMessage();
        // TODO !!!Add validation, model must be supported!
        if (isNullOrEmpty(model)) {
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to create completion for {} due to empty model", username);
            final var errorResponse = ChamberCompletionWithStreamResponseDto.builder() //
                    .content("No model found") //
                    .hasEnd(true) //
                    .build();
            return Flux.just(errorResponse);
        } else if (isNullOrEmpty(prompt)) {
            log.error(LOG_PREFIX_TIME_CHAMBER + "Unable to create completion between {} and {} due to empty message",
                    username, model);
            final var errorResponse = ChamberCompletionWithStreamResponseDto.builder() //
                    .content("No message found") //
                    .hasEnd(true) //
                    .build();
            return Flux.just(errorResponse);
        }
        final var openAICompletionRequest = OpenAICompletionRequestDto.builder() //
                .model(model) //
                .prompt(prompt) //
                // Must override default 16 limit
                .maxTokens(openAIProperties.maxTokensCompletion()) //
                .useStream(true) //
                .temperature(userCompletion.getTemperature()) //
                .build();
        final var startTimeMillis = System.currentTimeMillis();
        return openAIClientService.completionWithStream(openAICompletionRequest) //
                .map(this::extractAndCollectResponseMessage) //
                .filter(response -> response.content() != null) //
                .doOnComplete(() -> generateLog(startTimeMillis, username, model));
    }

    public String updateUserCompletion(final ChamberUpdateCompletionRequestBo request) {
        chamberCompletionLogic.updateUserCompletionByUsername(request);
        return "Success";
    }

    @Nonnull
    private ChamberCompletionWithStreamResponseDto extractAndCollectResponseMessage(final String openAIResponse) {
        if (openAIResponse.equals(END_OF_STREAM)) {
            return ChamberCompletionWithStreamResponseDto.builder() //
                    .content(END_OF_STREAM) //
                    .hasEnd(true) //
                    .build();
        }
        final var openAICompletionWithStreamResponseDto = parseJsonToObject(openAIResponse,
                OpenAICompletionWithStreamResponseDto.class);
        final var messageContent = openAICompletionWithStreamResponseDto != null ?
                openAICompletionWithStreamResponseDto.choices().get(0).text() :
                "";
        return ChamberCompletionWithStreamResponseDto.builder() //
                .content(messageContent) //
                .hasEnd(false) //
                .build();
    }

    private void generateLog(final long startTimeMillis, final String username, final String model) {
        final var endTimeMillis = System.currentTimeMillis();
        final var executionTimeSeconds = SharedFormatUtils.convertMillisToStringWithMaxTwoFractionDigits(
                endTimeMillis - startTimeMillis);
        log.info(LOG_PREFIX_TIME_CHAMBER + "Created completion between {} and {} in {}s", username, model,
                executionTimeSeconds);
    }

}