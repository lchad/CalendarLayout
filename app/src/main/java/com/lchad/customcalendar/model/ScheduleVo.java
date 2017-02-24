package com.lchad.customcalendar.model;

//  Created by liuchad on 16/3/21.
public class ScheduleVo {

    public long id; //事项ID
    public long uid; //事项所属用户ID
    public int type; //0、别人发布的事项同步到自己的日程 ; 1、自己发布的事项同步到自己的日程; 2、自己为自己创建的日程
    public String content;//事项内容
    public long scheduleTime; //完成时间
    public String createTime; //创建时间
    public String hostName; //发布人
    public int alertTime; //提醒时间    0 不提醒  1 15min  2 30min  4 1day
    public int alertType; //提醒类型    0 inApp  1 message  2 phoneCall
    public int priority;  //事项优先级  0 common 1 advanced
    public String mediaLocalPath; //本地音频地址
    public String attachmentDigest; //附件摘要

    public ScheduleVo() {
    }
}