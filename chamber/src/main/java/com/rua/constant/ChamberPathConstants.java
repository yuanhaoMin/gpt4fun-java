package com.rua.constant;

public class ChamberPathConstants {

    private ChamberPathConstants() {
    }

    public static final String CHAMBER_CONTROLLER_PATH = "/chamber";

    // Audio Controller
    public static final String CHAMBER_AUDIO_CONTROLLER_PATH = CHAMBER_CONTROLLER_PATH + "/audio";
    public static final String CHAMBER_AUDIO_TRANSCRIPTION_PATH = "/transcription";

    // Chat Completion Controller
    public static final String CHAMBER_CHAT_COMPLETION_CONTROLLER_PATH = CHAMBER_CONTROLLER_PATH + "/chat-completion";
    public static final String CHAMBER_CHAT_COMPLETION_WITHOUT_STREAM_PATH = "/messages";
    public static final String CHAMBER_CHAT_COMPLETION_WITH_STREAM_PATH = "/stream-messages";
    public static final String CHAMBER_CHAT_COMPLETION_RESET_CHAT_HISTORY_PATH = "/history";
    public static final String CHAMBER_CHAT_COMPLETION_UPDATE_SYSTEM_MESSAGE_PATH = "/system-message";

    // User Controller
    public static final String CHAMBER_USER_CONTROLLER_PATH = CHAMBER_CONTROLLER_PATH + "/user";
    public static final String CHAMBER_USER_LOGIN_PATH = "/login";
    public static final String CHAMBER_USER_REGISTER_PATH = "/register";

}