package com.neeko.map.common;

public class Constant {
    
    // 隐私级别
    public static final int PRIVACY_PRIVATE = 0;   // 仅自己可见
    public static final int PRIVACY_FRIEND = 1;    // 好友可见
    public static final int PRIVACY_PUBLIC = 2;    // 完全公开
    
    // 好友状态
    public static final int FRIEND_PENDING = 0;    // 待确认
    public static final int FRIEND_CONFIRMED = 1;  // 已是好友
    
    // 消息类型
    public static final int MSG_TYPE_TEXT = 1;     // 文字消息
    public static final int MSG_TYPE_IMAGE = 2;    // 图片消息
    
    // 消息已读状态
    public static final int MSG_UNREAD = 0;        // 未读
    public static final int MSG_READ = 1;          // 已读
}
