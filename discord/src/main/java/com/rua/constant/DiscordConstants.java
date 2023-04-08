package com.rua.constant;

public class DiscordConstants {

    // Warning
    public static final String EXCEED_MAX_RESPONSE_TOKENS = "系统: 由于ChatGPT回复所用的字数超过了预设的最大Token=%s的限制, 因此该回答已经被截短.";
    public static final String EXCEED_MAX_PROMPT_TOKENS = "系统: 清理聊天记录%d/%d -> %d/%d";

    // Command clear chat history
    public static final String CLEAR_CHAT_HISTORY_COMMAND_NAME = "clear";
    public static final String CLEAR_CHAT_HISTORY_COMMAND_DESCRIPTION = "清除聊天记录和对话情景, 下次对话将重新开始";
    public static final String CLEAR_CHAT_HISTORY_COMMAND_SUCCESS = "已清除聊天记录和对话情景";

    // Command set system message
    public static final String SET_SYSTEM_MESSAGE_COMMAND_NAME = "setsysmessage";
    public static final String SET_SYSTEM_MESSAGE_COMMAND_DESCRIPTION = "为ChatGPT设置对话情景";
    public static final String SET_SYSTEM_MESSAGE_COMMAND_FIRST_OPTION_NAME = "input";
    public static final String SET_SYSTEM_MESSAGE_COMMAND_FIRST_OPTION_DESCRIPTION = "对话情景, 如: 你是一个中国古代文官, 只用文言文回答问题, 语气阴阳怪气, 尖酸刻薄";
    public static final String SET_SYSTEM_MESSAGE_COMMAND_SUCCESS = "成功设置对话情景如下:\n%s";

}