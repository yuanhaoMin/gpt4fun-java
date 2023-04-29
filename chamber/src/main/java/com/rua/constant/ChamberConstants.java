package com.rua.constant;

public class ChamberConstants {

    private ChamberConstants() {

    }

    // Log
    public static final String LOG_PREFIX_TIME_CHAMBER = "Chamber -- ";

    // Chat completion notification
    public static final String SYSTEM_NOTIFICATION = "系统通知: ";
    public static final String RESET_CHAT_HISTORY_SUCCESS = SYSTEM_NOTIFICATION + "已重置聊天";
    public static final String SET_SYSTEM_MESSAGE_SUCCESS = SYSTEM_NOTIFICATION + "成功设置对话情景如下\n%s";
    // Error message prefix
    public static final String ERROR_AUTHENTICATION_FAILED = LOG_PREFIX_TIME_CHAMBER + "用户名或密码错误\n";
    public static final String ERROR_CONFLICT_USERNAME = LOG_PREFIX_TIME_CHAMBER + "用户名已存在\n";
    public static final String ERROR_MESSAGES_TOO_LONG = LOG_PREFIX_TIME_CHAMBER + "文本长度超过AI处理上限, 已重置聊天, 请继续使用\n";
    public static final String ERROR_PROMPT_TOO_LONG = LOG_PREFIX_TIME_CHAMBER + "文本长度超过AI处理上限, 请缩短文本内容后继续使用\n";
    public static final String ERROR_STREAM_READ_TIMEOUT = LOG_PREFIX_TIME_CHAMBER + "读取超时, 请刷新页面\n";
    public static final String ERROR_NO_STREAM_READ_TIMEOUT = LOG_PREFIX_TIME_CHAMBER + "阻塞式回复读取超时, 建议使用流式回复\n";
    public static final String ERROR_UNKNOWN_EXCEPTION = LOG_PREFIX_TIME_CHAMBER + "未知错误\n";

}