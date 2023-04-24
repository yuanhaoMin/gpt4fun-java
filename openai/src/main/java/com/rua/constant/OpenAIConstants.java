package com.rua.constant;

public class OpenAIConstants {

    private OpenAIConstants() {
    }

    // Log
    public static final String LOG_PREFIX_OPENAI = "OpenAI -- ";

    // Url
    public static final String OPENAI_API_BASE_URL = "https://api.openai.com/v1";
    public static final String OPENAI_API_CHAT_COMPLETION_URL = "/chat/completions";
    public static final String OPENAI_API_TRANSCRIPTION_URL = "/audio/transcriptions";

    // Model
    public static final String OPENAI_MODEL_GPT_35_TURBO = "gpt-3.5-turbo";
    public static final String OPENAI_MODEL_WHISPER_1 = "whisper-1";

    // Chat completion message role
    public static final String CHAT_COMPLETION_ROLE_ASSISTANT = "assistant";
    public static final String CHAT_COMPLETION_ROLE_SYSTEM = "system";
    public static final String CHAT_COMPLETION_ROLE_USER = "user";

    // Other
    public static final String END_OF_CHAT_COMPLETION_STREAM = "[DONE]";

}