package com.rua.constant;

public class ChamberConstants {

    private ChamberConstants() {

    }

    // Log
    public static final String LOG_PREFIX_TIME_CHAMBER = "Chamber -- ";

    // Response
    public static final String SYSTEM_NOTIFICATION = "系统通知: ";
    // GPT3.5
    public static final String GPT_35_CHAT_BAD_REQUEST = SYSTEM_NOTIFICATION + "因聊天记录超过GPT3.5的Token上限无法继续对话, 已重置聊天\n";
    public static final String GPT_35_CHAT_READ_TIME_OUT = SYSTEM_NOTIFICATION + "因请求超时无法获取回答, 建议重置聊天\n";
    public static final String GPT_35_CHAT_TOKEN_LIMIT = SYSTEM_NOTIFICATION + "聊天记录即将达到GPT3.5的Token上限 %d/%d, 建议重置聊天\n";
    public static final String GPT_35_CHAT_CLEAN_HISTORY = SYSTEM_NOTIFICATION + "自动清理最早聊天记录 %d/%d -> %d/%d\n";
    public static final String GPT_35_CHAT_TRUNCATE_RESPONSE = SYSTEM_NOTIFICATION + "聊天记录已超过GPT3.5的Token上限%d, 此回答已被截短\n";
    public static final String GPT_35_RESET_CHAT_HISTORY_SUCCESS = SYSTEM_NOTIFICATION + "已重置聊天";
    public static final String GPT_35_SET_SYSTEM_MESSAGE_SUCCESS = SYSTEM_NOTIFICATION + "成功设置对话情景如下\n%s";

}