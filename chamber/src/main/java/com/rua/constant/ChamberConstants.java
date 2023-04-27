package com.rua.constant;

public class ChamberConstants {

    private ChamberConstants() {

    }

    // Log
    public static final String LOG_PREFIX_TIME_CHAMBER = "Chamber -- ";

    // Response
    public static final String SYSTEM_NOTIFICATION = "系统通知: ";
    // Chat completion notification
    public static final String CHAT_COMPLETION_BAD_REQUEST = SYSTEM_NOTIFICATION + "因聊天记录超过AI处理上限无法继续对话, 已删除记录并重置聊天, 请继续使用\n";
    public static final String CHAT_COMPLETION_READ_TIME_OUT = SYSTEM_NOTIFICATION + "因请求超时无法获取回答, 建议重置聊天\n";
    public static final String RESET_CHAT_HISTORY_SUCCESS = SYSTEM_NOTIFICATION + "已重置聊天";
    public static final String SET_SYSTEM_MESSAGE_SUCCESS = SYSTEM_NOTIFICATION + "成功设置对话情景如下\n%s";
    // Completion notification
    public static final String COMPLETION_BAD_REQUEST = SYSTEM_NOTIFICATION + "文本长度超过AI处理上限, 请缩短文本内容后继续使用\n";

}