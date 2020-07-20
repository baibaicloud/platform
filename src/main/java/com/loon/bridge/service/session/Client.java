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
 *   Loon  2019年11月2日 下午11:54:07  created
 */
package com.loon.bridge.service.session;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.loon.bridge.uda.entity.Device;

/**
 * 
 *
 * @author nbflow
 */
public class Client {

    private String token;
    private Device device;
    private Date lastDate;
    private String sn;

    private ArrayBlockingQueue<Message> msgList = new ArrayBlockingQueue<Message>(200);

    public Client(Device device) {
        this.device = device;
        this.lastDate = new Date();
        this.sn = device.getSn();
    }

    public Message pollMessage() {
        try {
            return msgList.poll(8, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void put(Message msg) {
        try {
            msgList.put(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return the lastDate
     */
    public Date getLastDate() {
        return lastDate;
    }

    /**
     * @param lastDate the lastDate to set
     */
    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     * @return the msgList
     */
    public ArrayBlockingQueue<Message> getMsgList() {
        return msgList;
    }

    /**
     * @param msgList the msgList to set
     */
    public void setMsgList(ArrayBlockingQueue<Message> msgList) {
        this.msgList = msgList;
    }

    /**
     * @return the sn
     */
    public String getSn() {
        return sn;
    }

    /**
     * @param sn the sn to set
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

}
