package com.rua.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OpenAIChatCompletionModelEnum implements OpenAIModelInfo {

    GPT35("gpt-3.5-turbo"), //
    GPT4("gpt-4");

    private final String modelName;

    OpenAIChatCompletionModelEnum(final String modelName) {
        this.modelName = modelName;
    }

    @JsonProperty
    @Override
    public String getModelName() {
        return modelName;
    }

}