package com.rua.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OpenAICompletionModelEnum implements OpenAIModelInfo {

    DAVINCI3("text-davinci-003");

    private final String modelName;

    OpenAICompletionModelEnum(final String modelName) {
        this.modelName = modelName;
    }

    @JsonProperty
    @Override
    public String getModelName() {
        return modelName;
    }

}