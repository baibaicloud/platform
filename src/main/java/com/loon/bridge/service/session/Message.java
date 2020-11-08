/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 loon. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of loon.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with loon.
 * 
 * Modified history:
 *   Loon  2019年11月3日 上午12:12:46  created
 */
package com.loon.bridge.service.session;

import java.util.Date;

/**
 * 
 *
 * @author nbflow
 */
public class Message {

    private Type event;
    private Object body;
    private Date ctime;

    public Message() {
        this.ctime = new Date();
    }

    public enum Type {
        /** 空 */
        NONE,

        /** 发起远程控制 */
        START_REMOTE_CONTROL,

        /** 关闭远程控制 */
        CLOSE_REMOTE_CONTROL,

        /** 关机 */
        SHUTDOWN,

        /** 添加网元邀请 */
        ADD_NE_INVITE,

        /** 刷新隧道列表 */
        TUNNEL_REFRESH_LIST,

        /** 下载文件 */
        DOWNLOAD_FILE
    }

    /**
     * @return the ctime
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * @param ctime the ctime to set
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * @return the event
     */
    public Type getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Type event) {
        this.event = event;
    }

    /**
     * @return the body
     */
    public Object getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(Object body) {
        this.body = body;
    }
}
