package com.bz.android.push;

import java.io.Serializable;
/**
  *  @author ZhangYi
  *  功能描述: 通用的推送消息结构体：仅仅服务于具体业务，区别于推送消息体
 *           服务于万物和棒棒糖，推送消息下发后，根据下发字段拼装成本结构
 *           分发给万物或棒棒糖，实现各自业务跳转
  *  时 间： 2020/7/14 3:43 PM
  */
public class BZPushMsg implements Serializable {
    public String url; //跳转协议
}
