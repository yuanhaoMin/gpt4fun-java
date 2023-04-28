package com.rua.constant;

import lombok.Getter;

@Getter
public enum OpenAICompletionModelEnums {

    DAVINCI3("text-davinci-003");

    private final String modelName;

    OpenAICompletionModelEnums(final String modelName) {
        this.modelName = modelName;
    }

}