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
 *   Loon  2019年11月15日 下午11:01:20  created
 */
package com.loon.bridge.service.remotecpe;

import java.util.Date;

import com.loon.bridge.core.comenum.RemoteProtocolType;
import com.loon.bridge.core.comenum.Status;

/**
 * 
 *
 * @author nbflow
 */
public class NatInfo {

    private String proxyName;
    private Status remoteack;
    private String ackuuid;
    private String username;
    private String password;
    private String uuid;
    private Long uid;
    private String selfname;
    private Long deviceId;
    private String deviceSn;
    private Integer serverPort;
    private String serverAddress;
    private Integer remotePort;
    private Integer localPort;
    private RemoteProtocolType tunnetType;
    private Date lastDate;
    private Date ctime;

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
     * @return the uid
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the serverPort
     */
    public Integer getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the serverAddress
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * @return the remotePort
     */
    public Integer getRemotePort() {
        return remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * @return the deviceId
     */
    public Long getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the selfname
     */
    public String getSelfname() {
        return selfname;
    }

    /**
     * @param selfname the selfname to set
     */
    public void setSelfname(String selfname) {
        this.selfname = selfname;
    }

    /**
     * @return the deviceSn
     */
    public String getDeviceSn() {
        return deviceSn;
    }

    /**
     * @param deviceSn the deviceSn to set
     */
    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    /**
     * @return the ackuuid
     */
    public String getAckuuid() {
        return ackuuid;
    }

    /**
     * @param ackuuid the ackuuid to set
     */
    public void setAckuuid(String ackuuid) {
        this.ackuuid = ackuuid;
    }

    /**
     * @return the remoteack
     */
    public Status getRemoteack() {
        return remoteack;
    }

    /**
     * @param remoteack the remoteack to set
     */
    public void setRemoteack(Status remoteack) {
        this.remoteack = remoteack;
    }

    /**
     * @return the localPort
     */
    public Integer getLocalPort() {
        return localPort;
    }

    /**
     * @param localPort the localPort to set
     */
    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    /**
     * @return the tunnetType
     */
    public RemoteProtocolType getTunnetType() {
        return tunnetType;
    }

    /**
     * @param tunnetType the tunnetType to set
     */
    public void setTunnetType(RemoteProtocolType tunnetType) {
        this.tunnetType = tunnetType;
    }

    /**
     * @return the proxyName
     */
    public String getProxyName() {
        return proxyName;
    }

    /**
     * @param proxyName the proxyName to set
     */
    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
