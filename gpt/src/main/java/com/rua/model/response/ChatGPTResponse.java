package com.rua.model.response;

import java.time.LocalDate;
import java.util.List;

public record ChatGPTResponse(String id, String object, String model, LocalDate created, List<Choice> choices,
                              Usage usage) {
}