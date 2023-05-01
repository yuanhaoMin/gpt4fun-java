package com.rua.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum OpenAIGeneralCompletionModelEnum {

    DAVINCI3("text-davinci-003", false), //
    GPT35("gpt-3.5-turbo", true), //
    GPT4("gpt-4", true);

    private final String modelName;

    private final boolean isChatCompletionModel;

    OpenAIGeneralCompletionModelEnum(final String modelName, final boolean isChatCompletionModel) {
        this.modelName = modelName;
        this.isChatCompletionModel = isChatCompletionModel;
    }

    @JsonProperty
    public String getModelName() {
        return modelName;
    }

    public boolean isChatCompletionModel() {
        return isChatCompletionModel;
    }

    public static OpenAIGeneralCompletionModelEnum get(String modelName) {
        return Arrays.stream(values()) //
                .filter(model -> model.getModelName().equals(modelName)) //
                .findFirst() //
                .orElseThrow(IllegalArgumentException::new);
    }

}