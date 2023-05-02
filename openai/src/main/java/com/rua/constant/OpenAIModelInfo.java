package com.rua.constant;

import java.util.Arrays;

public interface OpenAIModelInfo {

    String getModelName();

    static <T extends Enum<T> & OpenAIModelInfo> T get(String modelName, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(model -> model.getModelName().equals(modelName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}