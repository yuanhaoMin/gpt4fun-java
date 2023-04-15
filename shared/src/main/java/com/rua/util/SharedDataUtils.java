package com.rua.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.rua.constant.SharedConstants.LOG_PREFIX_SHARED;

@Slf4j
public class SharedDataUtils {

    private SharedDataUtils() {
    }

    public static <T> List<T> convertJsonToList(String json, Class<T> targetType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            if (isNullOrEmpty(json)) {
                return new ArrayList<>();
            }
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, targetType));
        } catch (JsonProcessingException e) {
            log.error(LOG_PREFIX_SHARED + "Error parsing json to list: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static String convertObjectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(LOG_PREFIX_SHARED + "Error converting object to json: {}", e.getMessage());
            return "";
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String toStringNullSafe(Object object) {
        return object != null ? object.toString() : "";
    }

}