package com.rua.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OpenAITranscriptionModelEnum implements OpenAIModelInfo {

    WHISPER1("whisper-1");

    private final String modelName;

    OpenAITranscriptionModelEnum(@JsonProperty("modelName") //
                                 final String modelName) {
        this.modelName = modelName;
    }

    @JsonProperty
    @Override
    public String getModelName() {
        return modelName;
    }

}