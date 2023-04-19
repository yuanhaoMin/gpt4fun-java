package com.rua.constant;

public class OpenAIConstants {

    private OpenAIConstants() {
    }

    // Log
    public static final String LOG_PREFIX_OPENAI = "OpenAI -- ";

    // Url
    public static final String OPENAI_API_BASE_URL = "https://api.openai.com/v1";
    public static final String OPENAI_API_CHAT_URL = "/chat/completions";
    public static final String OPENAI_API_CREATE_TRANSCRIPTION_URL = "/audio/transcriptions";

    // Model
    public static final String OPENAI_MODEL_GPT_35_TURBO = "gpt-3.5-turbo";
    public static final String OPENAI_MODEL_WHISPER_1 = "whisper-1";

    // gpt-3.5-turbo message role
    public static final String GPT35TURBO_ASSISTANT = "assistant";
    public static final String GPT35TURBO_SYSTEM = "system";
    public static final String GPT35TURBO_USER = "user";

}